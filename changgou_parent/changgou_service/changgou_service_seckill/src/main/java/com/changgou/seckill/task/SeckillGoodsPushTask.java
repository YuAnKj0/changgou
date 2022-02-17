package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 添加秒杀定时任务
 */
@Component
public class SeckillGoodsPushTask {

//    业务逻辑：
//            1）获取秒杀时间段菜单信息
//            2）遍历每一个时间段，添加该时间段下秒杀商品
//
//                2.1）将当前时间段转换为String，作为redis中的key
//
//                2.2）查询商品信息（状态为1，库存大于0，秒杀商品开始时间大于当前时间段，秒杀商品结束时间小于当前时间段，当前商品的id不在redis中）
//
//           3）添加redis

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    public static final String SECKILL_GOODS_KEY="seckill_goods_";
    public static final String SECKILL_GOODS_STOCK_COUNT_KEY="seckill_goods_stock_count_";

    @Scheduled(cron = "0/30 * * * * ？")
    public void loadSeckillGoodsToRedis(){
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            //每次用最好重新new
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String redisExName=DateUtil.date2Str(dateMenu);
            Example example=new Example(SeckillGoods.class);
            Example.Criteria criteria= example.createCriteria();
            criteria.andEqualTo("status","1");
            criteria.andGreaterThan("stockCount","0");
            criteria.andGreaterThanOrEqualTo("startTime",simpleDateFormat.format(dateMenu));
            criteria.andLessThan("endTime",simpleDateFormat1.format(DateUtil.addDateHour(dateMenu,2)));
            Set keys=redisTemplate.boundHashOps(SECKILL_GOODS_KEY+redisExName).keys();
            if (keys!=null&&keys.size()>0){
                criteria.andNotIn("id",keys);
            }

            //添加到缓存
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps(SECKILL_GOODS_KEY+redisExName).put(seckillGoods.getId(),seckillGoods);

                //加载秒杀商品的库存
                //预扣减缓存中的库存再异步扣减mysql数据。
                redisTemplate.opsForValue().set(SECKILL_GOODS_STOCK_COUNT_KEY+seckillGoods.getId(),seckillGoods.getStockCount());


            }
        }
    }


}
