package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "店铺管理")
@RequestMapping("/admin/shop")
@RestController("adminShopController")
public class ShopController {

    public static final String SHOP_STATUS_KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态为：{}", status==1?"营业中":"休息中");
        redisTemplate.opsForValue().set(SHOP_STATUS_KEY, status);
        return Result.success();
    }

    /**
     * 查询店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺状态")
    public Result<Integer> getStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        log.info("查询店铺状态为{}", shopStatus==1?"营业中":"休息中");
        return Result.success(shopStatus);
    }


}
