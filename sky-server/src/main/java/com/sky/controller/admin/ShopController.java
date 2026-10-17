package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("AdminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺状态类接口")
public class ShopController {

    private static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 更改店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result switchStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为{}", status == 1 ? "营业中" : "打烊中");
        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY, status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        Integer status = valueOperations.get(KEY);
        log.info("查询店铺营业状态,status={}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
