package feign;

import com.changgou.content.pojo.Content;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
