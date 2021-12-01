package com.itheima.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.itheima.canal.config.RabbitMQConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ZJ
 */
@CanalEventListener
public class BusinessListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     *
     * @param eventType 当前操作数据库的类型
     * @param rowData   当前操作数据库的的数据
     */
    @ListenPoint(schema = "changgou_business",table = "tb_ad")
    public void adUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("广告表数据发生改变");

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            if ("position".equals(column.getName())){
                System.out.println("发送最新的数据到MQ:"+column.getValue());

                //发送消息
                rabbitTemplate.convertAndSend("", RabbitMQConfig.AD_UPDATE_QUEUE,column.getValue());
            }
        }
    }
}
