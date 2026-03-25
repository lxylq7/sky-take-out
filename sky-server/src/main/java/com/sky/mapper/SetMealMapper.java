package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {


    /**
     * 新增套餐
     * @param setMeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setMeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐
     * @param id
     */
    @Delete("delete from setmeal where id = #{id}")
    void delete(Long id);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);

    /**
     * 更新套餐
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * 查询套餐列表
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);
}
