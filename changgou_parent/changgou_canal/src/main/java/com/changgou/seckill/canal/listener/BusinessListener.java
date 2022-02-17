package com.changgou.seckill.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.seckill.canal.config.RabbitMQConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 19:04 2021/12/5
 * @Modified By:
 */
@CanalEventListener //声明当前的类是canalDe监听类
public class BusinessListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;//消息队列模板类


    /**
     *
     * @param eventType 当前操作数据库的类型
     * @param rowData   数据
     *                  schema  数据库
     *                  table   biaoming
     *
     */
    @ListenPoint(schema = "changgou_business",table = "tb_ad")
    public void adUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){

        System.out.println("广告表数据发生改变");
        //获取改变前的数据
        //rowData.getBeforeColumnsList().forEach((c)-> System.out.println("改变前的数据：" + c.getName() + "::  " + c.getValue()));

        //获取改变后的数据
        //rowData.getAfterColumnsList().forEach((c)-> System.out.println("改变后的数据："+c.getName()+"::" +c.getValue()));

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            if ("position".equals(column.getName())) {
                System.out.println("发送最新数据到MQ："+ column.getValue());


                //发送消息
                rabbitTemplate.convertAndSend("", RabbitMQConfig.AD_UPDATE_QUEUE,column.getValue());

            }

        }

    }
}
