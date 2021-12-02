package com.changgou.listener;


import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

/**
 * @author Ykj
 * @ClassName CanalDataEventListener
 * @Discription
 * @date 2021/12/2 8:46
 */
@CanalEventListener
public class CanalDataEventListener {

    /**
     * 增加数据监听
     * @param eventType
     * @param rowData
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }

    /**
     * 修改数据监听
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData){

        System.out.println("UpdateListenPoint");
        rowData.getAfterColumnsList().forEach((c)->System.out.println("By--Annotation: "+c.getName()+"::  "+c.getValue()));
    }

    /**
     * 删除数据监听
     * @param eventType
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType){
        System.out.println("DeleteListenPoint");
    }

    @ListenPoint(destination = "example",schema = "changgou_content",table = {"tb_content_category","tb_content"},eventType = CanalEntry.EventType.UPDATE)
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("DeleteListenPoint");
        rowData.getAfterColumnsList().forEach((c)-> System.out.println("By--Annotation: "+ c.getName()+"::  " +c.getValue()));
    }


}
