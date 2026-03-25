package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetMealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "用户端-套餐管理")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    /**
     * 查询套餐列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询套餐列表")
    @Cacheable(cacheNames = "setmealCache",key="#categoryId") //key = setmealCache :: 100
    public Result<List<Setmeal>> selectSetmealList(Long categoryId){
        log.info("查询套餐列表");
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(StatusConstant.ENABLE);
        setmeal.setCategoryId(categoryId);
        List<Setmeal> setmealList = setMealService.list(setmeal);
        return Result.success(setmealList);
    }

    /**
     * 查询套餐下的菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("查询套餐列表")
    public Result<List<DishItemVO>> selectDishBySetmealID(@PathVariable Long id){
        List<DishItemVO> dishItemVOList = setMealService.selectDishBySetmealID(id);
        return Result.success(dishItemVOList);
    }


}
