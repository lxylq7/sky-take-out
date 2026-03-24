package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
     * @param
     * @return
     */
    //select dish_if from setmeal_dishId where dish_id in (1,2,3,4)
    List<Long> getSetMealDishIds(List<Long> dishIds);

    /**
     * 更新套餐菜品状态
     * @param id
     */
    @Update("update setmeal set status = #{status} where id = #{id}")
    void updateStatusById(Long id);

    /**
     * 更新套餐菜品
     * @param setMeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setMeal);

    /**
     * 插入套餐和菜品关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品关系
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id查询套餐和菜品关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);
}