package com.changgou.content.service.impl;

import com.changgou.content.pojo.Content;
import com.changgou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Ykj
 * @ClassName ContentServiceImpl
 * @Discription
 * @date 2021/12/2 10:10
 */
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    /**
     * 根据分分类查询id
     * @param id
     * @return
     */
    @Override
    public List<Content> findByCategory(Long id) {
        Content content=new Content();
        content.setCategoryId(id);
        content.setStatus("1");
        return contentMapper.select(content);
    }
}
