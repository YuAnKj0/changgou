package com.changgou.seckill.token;

import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName TokenTest
 * @Discription
 * @date 2021/12/10 10:56
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TokenTest {

    @Autowired
    private  LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void applyToken(){

        //构建请求地址(euraka注册中心的形式获取)
        ServiceInstance serviceInstance = loadBalancerClient.choose("USER-AUTH");
        URI uri = serviceInstance.getUri();
        String url=uri+"/oauth/token";
        //封装请求参数
        //body ,headers
        MultiValueMap<String, String> body=new LinkedMultiValueMap<>();
        body.add("grand_type","password");
        body.add("username","itheima");
        body.add("password","itheima");

        MultiValueMap<String, String> headers=new LinkedMultiValueMap<>();
        headers.add("Authorization",this.getHttpBasic("changgou","changgou"));
        HttpEntity<MultiValueMap<String, String>> requestEntity=new HttpEntity<>(body,headers);

        //当客户端出现400或者401时，后端不对这两个异常编码进行处理，而是直接返回给前端
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //发送请求
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map map = responseEntity.getBody();
        System.out.println(map);

    }

    private String getHttpBasic(String clientId, String clientSecret) {
        String value=clientId+":"+clientSecret;
        byte[] encode = Base64Utils.encode(value.getBytes());
        //
        return "Basic "+new String(encode);
    }
}
