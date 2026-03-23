package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 新增分类
     * @param
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 分类启用/禁用
     * @param status
     * @param id
     */
    void categoryStartOrStop(Integer status, Long id);

    /**
     * 删除分类
     * @param id
     */
    void categoryDelete(Integer id);

    /**
     * 更新分类
     * @param categoryDTO
     */
    void categoryUpdate(CategoryDTO categoryDTO);

    /**
     * 根据分类类型查询分类
     * @return
     */
    List<Category> selectByType(Integer type);
}
