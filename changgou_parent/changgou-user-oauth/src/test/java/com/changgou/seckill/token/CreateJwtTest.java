package com.changgou.seckill.token;

import com.alibaba.fastjson.JSON;
import java.security.interfaces.RSAPrivateKey;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:42
 * @Description: com.changgou.token
 *      创建JWT令牌，使用私钥加密
 ****/
public class CreateJwtTest {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken(){


        //创建秘钥工厂
        //1、私钥的位置
        //2/密钥库的密码
        ClassPathResource classPathResource=new ClassPathResource("changgou.jks");
        String keyPath="changgou";

        KeyStoreKeyFactory keyStoreKeyFactory =new KeyStoreKeyFactory(classPathResource,keyPath.toCharArray());


        //获取私钥

        String alias="changgou";
        String password="changgou";
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, password.toCharArray()); //私钥
        //将私钥转换为rsa的
        RSAPrivateKey rsaPrivateKey= (RSAPrivateKey) keyPair.getPrivate();

        //生成Jwt令牌

        Map<String, String> map=new HashMap();
        map.put("company","heima");
        map.put("address","beijing");
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(rsaPrivateKey));


        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
