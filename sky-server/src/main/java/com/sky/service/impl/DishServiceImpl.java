package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品和口味表
     */
    @Override
    @Transactional
    public void saveWithFlovor(@RequestBody DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入一条数据
        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断是否能够删除 启用中？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否套餐关联
        List<Long> setMealDishIds = setMealDishMapper.getSetMealDishIds(ids);
        if (setMealDishIds != null && setMealDishIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据 直接删除
            dishFlavorMapper.deleteByDishId(id);
        }*/
        //sql : delete from dish where id in (1,2,3,4)
        //sql : delete from dish_flavor where dish_id in (1,2,3,4)
        //批量删除菜品
        dishMapper.deleteByIds(ids);
        //批量删除口味
        dishFlavorMapper.deleteByDishIds(ids);


    }

    /**
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //查询菜品数据 查询口味数据 封装到VO中
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavor = dishFlavorMapper.getByDishId(id);
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavor);
        return dishVO;
    }

    /**
     * 更新菜品和口味表
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品表
        dishMapper.update(dish);
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 更新菜品状态
     * @param status
     */
    @Override
    public void updateStatus(Integer status,Long id) {
        //需要根据id更改状态
        Dish dish = Dish.builder()
                        .id(id)
                        .status(status)
                        .build();
        dishMapper.update(dish);
        //菜品停售了 同时套餐也要停售
        //判断是否是停售操作
        if (status == StatusConstant.DISABLE){
            //判断是否有套餐 创建一个集合
            ArrayList<Long> ids = new ArrayList<>();
            ids.add(id);
            List<Long> setMealDishIds = setMealDishMapper.getSetMealDishIds(ids);
            if (setMealDishIds != null && setMealDishIds.size() > 0) {
                //能进来说明有套餐关联 需要停用对应套餐 可能存在多个套餐
                for (Long setMealId : setMealDishIds) {
                    Setmeal setMeal = Setmeal.builder()
                            .id(setMealId)
                            .status(status)
                            .build();
                    setMealDishMapper.update(setMeal);
                }
            }
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> deleteByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 根据分类id查询菜品和口味
     * @param dish
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> list = dishMapper.list(dish);

        List<DishVO> listVO = new ArrayList<>();
        for (Dish d : list) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            //查询口味数据
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            listVO.add(dishVO);
        }
        return listVO;
    }

    /**
     * 根据分类id查询菜品和口味
     * @param categoryId
     * @return
     */
    /*@Override
    public List<DishVO> getByCategoryIdWithFlavor(Long categoryId) {
        List<Dish> list = dishMapper.getByCategoryId(categoryId);
        List<DishVO> listVO = new ArrayList<>();
        for (Dish dish : list) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish,dishVO);
            //第一编写没查口味
            List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(dish.getId());
            dishVO.setFlavors(dishFlavor);
            listVO.add(dishVO);
        }
        return listVO;
        }*/



}
