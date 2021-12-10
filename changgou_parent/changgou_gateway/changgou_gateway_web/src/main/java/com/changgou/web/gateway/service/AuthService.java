package com.changgou.web.gateway.service;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

/**
 * @author Ykj
 * @ClassName AuthService
 * @Discription
 * @date 2021/12/10 16:44
 */
@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //从cookie中获取jti的值

    public String getJtiFromCookie(ServerHttpRequest request) {
        HttpCookie httpCookie=request.getCookies().getFirst("uid");
        if (httpCookie!=null) {
            String jti = httpCookie.getValue();
            return jti;
        }
        return null;
    }

    public String getJwtFromRedis(String jti) {
        String jwt = stringRedisTemplate.boundValueOps(jti).get();
        return jwt;
    }
}
