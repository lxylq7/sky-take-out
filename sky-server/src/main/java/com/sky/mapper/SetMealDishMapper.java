package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据套餐id列表查询菜品id列表
     * @param ids
     * @return
     */
    //select dish_if from setmeal_dishId where dish_id in (1,2,3,4)
    List<Long> getSetMealDishIds(List<Long> ids);
}