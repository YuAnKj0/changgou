package com.changgou.listener;


import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;

import com.xpand.starter.canal.annotation.*;
import entity.Result;
import feign.ContentFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import pojo.Content;


import java.util.List;



/**
 * @author Ykj
 * @ClassName CanalDataEventListener
 * @Discription
 * @date 2021/12/2 8:46
 */
@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    /**
//     * 增加数据监听
//     * @param eventType
//     * @param rowData
//     */
//    @InsertListenPoint
//    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
//        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }
//
//    /**
//     * 修改数据监听
//     * @param rowData
//     */
//    @UpdateListenPoint
//    public void onEventUpdate(CanalEntry.RowData rowData){
//
//        System.out.println("UpdateListenPoint");
//        rowData.getAfterColumnsList().forEach((c)->System.out.println("By--Annotation: "+c.getName()+"::  "+c.getValue()));
//    }
//
//    /**
//     * 删除数据监听
//     * @param eventType
//     */
//    @DeleteListenPoint
//    public void onEventDelete(CanalEntry.EventType eventType){
//        System.out.println("DeleteListenPoint");
//    }

    /**
     * 自定义的监听
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",
            schema = "changgou_content",
            table = {"tb_content_category","tb_content"},
            eventType ={CanalEntry.EventType.UPDATE,
                        CanalEntry.EventType.DELETE,
                        CanalEntry.EventType.INSERT} )
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //获取列名category_id的值
        String categoryId=getColumnValue(eventType,rowData);
        //调用feign获取给分类下的所有广告的集合
        Result<List<Content>> categoryResult=contentFeign.findByCategory(Long.valueOf(categoryId));

        //使用redisTemplate存储到redis中
        List<Content> data=categoryResult.getData();
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId="";
        //判断 如果是删除，则获取beforelist
        if (eventType== CanalEntry.EventType.DELETE) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id_")) {
                    categoryId=column.getValue();
                    return categoryId;
                }
                
            }
        }else {
            //判断  如果是添加或更新，则获取afterlist
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id_")) {
                    categoryId=column.getValue();
                    return categoryId;
                }
                
            }
        }
        return categoryId;
    }


}
