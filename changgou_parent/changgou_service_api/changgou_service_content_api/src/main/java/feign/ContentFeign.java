package feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.Content;

import java.util.List;

/**
 * @author Ykj
 * @ClassName ContentFeign
 * @Discription
 * @date 2021/12/2 13:18
 */

@FeignClient(name = "content")
@RequestMapping(value = "/content")
public interface ContentFeign {
    @GetMapping("/list/category/{id}")
    Result<List<Content>> findByCategory(@PathVariable Long id);
}
