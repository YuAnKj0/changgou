package com.changgou.content.feign;

import com.changgou.content.pojo.Content;
import entity.Result;

import java.util.List;

public interface ContentFeign {
    Result<List<Content>> findByCategory(Long valueOf);
}
