package com.changgou.seckill.gateway.filter;

import com.changgou.seckill.gateway.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * @author Ykj
 * @ClassName AuthFilter
 * @Discription
 * @date 2021/12/10 16:37
 */

@Component
public class AuthFilter implements GlobalFilter, Ordered  {

    @Autowired
    private AuthService authService;
    private static final String LOGIN_URL="http://localhost:8001/api/oauth/toLogin";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断当前请求路径是否为登录请求，如果是，则直接放行
        String path = request.getURI().getPath();
        if ("/api/oauth/login".equals(path)|| !UrlFilter.hasAuthorized(path)) {
            //直接放行
            return chain.filter(exchange);
        }
        //2.从cookie中获取jti的值，如果该值不存在，拒绝本次访问
        String jti=authService.getJtiFromCookie(request);
        if (StringUtils.isEmpty(jti)){
            //jujue 访问
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
            //跳转登录页面
            return this.toLoginPage(LOGIN_URL+"FROM"+request.getURI().getPath(),exchange);

        }

        //3.从redis中获取jwt的值，如果改制不存在，拒绝本次访问
        String jwt=authService.getJwtFromRedis(jti);
        if (StringUtils.isEmpty(jwt)) {
            //jujue 访问
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
            //跳转登录页面
            return this.toLoginPage(LOGIN_URL,exchange);
        }

        //4.对当前的请求对象增强，让他携带令牌的信息
        request.mutate().header("Authorization","Bearer "+jwt);


        return chain.filter(exchange);
    }

    //跳转登录页
    private Mono<Void> toLoginPage(String loginUrl, ServerWebExchange exchange) {
        ServerHttpResponse response=exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("location",loginUrl);
        return response.setComplete();
    }

    //设置当前过滤器的执行优先级，值越小，优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
