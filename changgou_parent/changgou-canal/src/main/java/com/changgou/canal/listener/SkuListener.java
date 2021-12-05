package com.changgou.canal.listener;

import com.changgou.canal.config.RabbitMQConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ykj
 * @Description:
 * @Date: Created in 22:51 2021/12/5
 * @Modified By:
 */
@CanalEventListener
public class SkuListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @ListenPoint(schema = "changgou_goods",table = "tb_spu")
    public void goodsUp(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //改变之前的数据,bingjiang这部分数据转换为map
        Map<String, String> oldData = new HashMap<>();
        rowData.getBeforeColumnsList().forEach((c)-> oldData.put(c.getName(),c.getValue()));


        //改变之后的数据,bingjiang这部分数据转换为map
        Map<String, String> newData=new HashMap<>();
        rowData.getAfterColumnsList().forEach((c)-> oldData.put(c.getName(),c.getValue()));

        //获取最新上架的商品 0->1
        if ("0".equals(oldData.get("is_marketable"))&&"1".equals(newData.get("is_marketable"))) {
            //将商品的spuid发送到mq
            rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_UP_EXCHANGE,"",newData.get("id"));

        }


    }
}
