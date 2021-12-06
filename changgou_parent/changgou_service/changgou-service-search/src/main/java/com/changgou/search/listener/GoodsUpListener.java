package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ykj
 * @ClassName GoodsUpListener
 * @Discription
 * @date 2021/12/6 11:17
 */
@Component
public class GoodsUpListener {

    @Autowired
    private ESManagerService esManagerService;

    @RabbitListener(queues = RabbitMQConfig.SEARCH_ADD_QUEUE)
    public void receiveMessage(String spuId){
        //查询skuList,并导入到索引库
        esManagerService.importDataBySpuId(spuId);
    }

}
