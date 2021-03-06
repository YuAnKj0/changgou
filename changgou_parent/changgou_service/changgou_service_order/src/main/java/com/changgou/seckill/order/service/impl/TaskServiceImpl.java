package com.changgou.seckill.order.service.impl;

import com.changgou.seckill.order.dao.TaskHisMapper;
import com.changgou.seckill.order.dao.TaskMapper;
import com.changgou.seckill.order.pojo.Task;
import com.changgou.seckill.order.pojo.TaskHis;
import com.changgou.seckill.order.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Ykj
 * @ClassName TaskServiceImpl
 * @Discription
 * @date 2021/12/14 13:34
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskHisMapper taskHisMapper;
    @Autowired
    private TaskMapper taskMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delTask(Task task) {
        //1.记录删除时间
        task.setDeleteTime(new Date());
        Long taskId= task.getId();

        //bean的拷贝：属性名称需要一致
        TaskHis taskHis=new TaskHis();
        BeanUtils.copyProperties(task,taskHis);

        //记录历史任务数据
        taskHisMapper.insertSelective(taskHis);
        //删除原有任务数据
        task.setId(taskId);
        taskMapper.deleteByPrimaryKey(task);
        System.out.println("当前的订单服务完成了添加历史任务并删除原有任务的操作");
    }
}
