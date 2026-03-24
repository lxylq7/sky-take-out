package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealMapper {

    /**
     * 新增套餐
     * @param setMeal
     */
    @Insert("insert into setmeal(category_id,name,price,status,description,image,create_time,update_time,create_user,update_user) " +
            "values(#{categoryId},#{name},#{price},#{status},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setMeal);
}
