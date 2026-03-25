package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void setMealSave(SetmealDTO setmealDTO) {
        Setmeal setMeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setMeal);
        setMealMapper.insert(setMeal);
        //保持菜品和套餐的联系
        //获取生成的套餐id
        Long setmealId = setMeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
                });
        //向setmeal_dish 表中批量插入
        setMealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 删除套餐 可批量删除
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        //起售中的套餐不能删除
        ids.forEach(id -> {
            Setmeal setmeal = setMealMapper.selectById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(setmealId -> {
            setMealMapper.delete(setmealId);
            setMealDishMapper.deleteBySetmealId(setmealId);
        });
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO selectDishById(Long id) {
        Setmeal setmeal = setMealMapper.selectById(id);
        List<SetmealDish> setmealDishs = setMealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishs);
        return setmealVO;
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);
        //删除套餐和菜品的关联关系 在重新插入
        Long id = setmealDTO.getId();
        setMealDishMapper.deleteBySetmealId(id);
        List<SetmealDish> setmealDishs = setmealDTO.getSetmealDishes();
        setmealDishs.forEach(setmealDish -> {
            setmealDish.setSetmealId(id);
        });
        setMealDishMapper.insertBatch(setmealDishs);
    }

    /**
     * 更新套餐状态
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        //起售套餐时候 查看是否有菜品停售 如果有 不能起售
        if (status == StatusConstant.ENABLE) {
            //通过id查询菜品 id是套餐id
            List<Dish> dishes = dishMapper.selectBySetmealId(id);
            dishes.forEach(dish -> {
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setMealMapper.update(setmeal);
    }

    /**
     * 查询套餐列表
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setMealMapper.list(setmeal);
    }

    /**
     * 根据套餐id查询套餐下的菜品
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> selectDishBySetmealID(Long setmealId) {
        return setMealDishMapper.selectDishBySetmealID(setmealId);
    }


}
