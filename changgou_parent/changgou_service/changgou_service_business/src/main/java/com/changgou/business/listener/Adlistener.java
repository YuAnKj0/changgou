package com.changgou.business.listener;

import okhttp3.*;
import okhttp3.Request.Builder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 22:21 2021/12/5
 * @Modified By:
 */

@Component
public class Adlistener {

    @RabbitListener(queues = "ad_update_queue")
    public void receiveListener(String message){

        System.out.println("接收到的消息为"+message);

        //发起远程调用
        OkHttpClient okHttpClient=new OkHttpClient();
        String url= "http://192.168.211.132/ad_update?positon="+message;
        Request request=new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功
                System.out.println("请求成功："+response.message());

            }
        });

    }
}
