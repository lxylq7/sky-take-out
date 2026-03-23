package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("categoryPageQueryDTO = {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 分类启用/禁用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("分类启用/禁用")
    public Result categoryStartOrStop(@PathVariable Integer status,Long id) {
        categoryService.categoryStartOrStop(status,id);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result categoryDelete(Integer id) {
        log.info("删除分类id = {}", id);
        categoryService.categoryDelete(id);
        return Result.success();
    }

    /**
     * 更新分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新分类")
    public Result categoryUpdate(@RequestBody CategoryDTO categoryDTO) {
        log.info("更新分类categoryDTO = {}", categoryDTO);
        categoryService.categoryUpdate(categoryDTO);
        return Result.success();
    }

    /**
     * 根据分类类型查询分类
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类类型查询分类")
    public Result selectByType(Integer type){
        log.info("根据分类类型查询分类type = {}", type);
        List<Category> categroyList = categoryService.selectByType(type);
        return Result.success(categroyList);
    }

}
