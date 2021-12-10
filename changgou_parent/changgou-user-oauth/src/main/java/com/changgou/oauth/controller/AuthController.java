package com.changgou.oauth.controller;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 16:42
 * @Description: com.changgou.oauth.controller
 ****/
@RestController
@RequestMapping(value = "/oauth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret")
    private String clientSecret;
    @Value("${auth.cookieDomain")
    private String cookieDomain;
    @Value("${auth.cookieMaxAge")
    private int cookieMaxAge;



    @RequestMapping("/login")
    @ResponseBody
    public Result login(String username,String password,HttpServletResponse response){
        //校验参数
        if (StringUtils.isEmpty(username)) {
            throw new RuntimeException("请输入用户名");
        }
        if (StringUtils.isEmpty(password)) {
            throw new RuntimeException("请输入密码");
        }

        //申请令牌authToken

        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        //Jiangjti的值存入cookie中
        this.saveJtiToCookie(authToken.getJti(),response);

        //返回结果
        return  new Result(true,StatusCode.OK,"登录成功",authToken.getJti());


    }

    //将令牌的短标识jti存入到cookie中
    private void saveJtiToCookie(String jti, HttpServletResponse response) {
        CookieUtil.addCookie(response,cookieDomain,"/","uid",jti,cookieMaxAge,false);


    }


}
