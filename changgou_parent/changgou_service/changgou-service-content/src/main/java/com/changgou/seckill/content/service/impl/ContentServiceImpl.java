package com.changgou.seckill.content.service.impl;


import com.changgou.seckill.content.dao.ContentMapper;
import com.changgou.seckill.content.service.ContentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import pojo.Content;
import tk.mybatis.mapper.entity.Example;

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
     * Content条件+分页查询
     * @param content   查询条件
     * @param page  页码
     * @param size  页大小
     * @return  分页结果
     */
    @Override
    public PageInfo<Content> findPage(Content content, int page, int size) {
        PageHelper.startPage(page,size);
        Example example=createExample(content);
        //执行搜索
        return new PageInfo<Content>(contentMapper.selectByExample(example));
    }

    /**
     * Conetent构建查询对象
     * @param content
     * @return
     */
    private Example createExample(Content content) {
        Example example=new Example(Content.class);
        Example.Criteria criteria=example.createCriteria();
        if (content!=null) {
            if (!StringUtils.isEmpty(content.getId())) {
                criteria.andEqualTo("id",content.getId());
            }
            //内容类目id
            if (!StringUtils.isEmpty(content.getCategoryId())) {
                criteria.andEqualTo("categoryId",content.getCategoryId());
            }
            // 内容标题
            if (!StringUtils.isEmpty(content.getTitle())) {
                criteria.andEqualTo("title",content.getTitle());
            }
            // 链接
            if(!StringUtils.isEmpty(content.getUrl())){
                criteria.andEqualTo("url",content.getUrl());
            }
            // 图片绝对路径
            if(!StringUtils.isEmpty(content.getPic())){
                criteria.andEqualTo("pic",content.getPic());
            }
            // 状态,0无效，1有效
            if(!StringUtils.isEmpty(content.getStatus())){
                criteria.andEqualTo("status",content.getStatus());
            }
            // 排序
            if(!StringUtils.isEmpty(content.getSortOrder())){
                criteria.andEqualTo("sortOrder",content.getSortOrder());
            }
        }
        return example;
    }

    @Override
    public PageInfo<Content> findPage(int page, int size) {
        return null;
    }

    @Override
    public List<Content> findList(Content content) {
        return null;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id) {
        contentMapper.deleteByPrimaryKey(id);

    }

    /**
     * 修改
     * @param content
     */
    @Override
    public void update(Content content) {
        contentMapper.updateByPrimaryKey(content);

    }

    /**
     * 添加Content
     * @param content
     */
    @Override
    public void add(Content content) {
        contentMapper.insert(content);

    }

    /**
     * 根据id查询content
     * @param id
     * @return
     */
    @Override
    public Content findById(Long id) {

        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 查找全部content数据
     * @return
     */
    @Override
    public List<Content> findAll() {
        return contentMapper.selectAll();
    }

    /**
     * 根据分类查询id
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
