package com.changgou.content.service;

import com.changgou.content.pojo.Content;
import org.springframework.boot.autoconfigure.web.ResourceProperties;

import java.util.List;

/**
 * @author Ykj
 * @ClassName ContentService
 * @Discription
 * @date 2021/12/2 10:06
 */
public interface ContentService {

    List<Content> findByCategory(Long id);
}
