package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "用户端-根据分类id查询菜品")
@Slf4j
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;

    /*@GetMapping("/list")
    @ApiOperation(value = "用户端-根据分类id查询菜品")
    public Result<List<DishVO>> selectByType(@RequestParam Long categoryId){

        List<DishVO>dishVOList = dishService.getByCategoryIdWithFlavor(categoryId);

        return Result.success(dishVOList);
    }
    */

    @GetMapping("/list")
    @ApiOperation(value = "用户端-根据分类id查询菜品")
    public Result<List<DishVO>> selectByType(@RequestParam Long categoryId){
        //构造redis中的key dish_分类id
        String key = "dish" + categoryId;
        //检查redis中是否存有菜品数据
        List<DishVO> redisList= (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (redisList != null && redisList.size()>0) {
            //如果存在 直接返回 无须查询数据库
            return Result.success(redisList);
        }
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        //如果不存在 查询数据库 将查询到的数据放入redis中
        List<DishVO> list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}
