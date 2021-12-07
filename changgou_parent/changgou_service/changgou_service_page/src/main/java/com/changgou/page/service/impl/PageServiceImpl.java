package com.changgou.page.service.impl;

import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Spu;
import com.changgou.page.service.PageService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ykj
 * @ClassName PageServiceImpl
 * @Discription
 * @date 2021/12/7 17:09
 */
@Service
public class PageServiceImpl implements PageService {




    @Override
    public void generateHtml(String souId) {
        //1.获取context对象，用于存储商品的相关数据
        Context context=new Context();
        //获取静态化页面的相关数据
        Map<String, Object> itemData=this.getItemData(souId);
        context.setVariables(itemData);

        //2.获取商品详情页面的存储位置
        //3.判断当前存储位置的文件夹是否存在，如不存在则新建
        //4.定义一个输出流，完成文件的生成
        //5.关闭流
    }

    @Autowired
    private SpuFeign spuFeign;


    //获取静态化页面的相关数据
    private Map<String, Object> getItemData(String souId) {
        Map<String, Object> resultMap=new HashMap<>();
        //获取spu
        Spu spu = spuFeign.findSpuById(souId).getData();
        resultMap.put("spu",spu);
        //获取图片信息
        if (spu!=null) {
            if (StringUtils.isNotEmpty(spu.getImages())) {
                resultMap.put("imageList",spu.getImages().split(","));
            }
        }
        //获取当前商品的分类信息
        //获取sku的相关信息

    }
}
