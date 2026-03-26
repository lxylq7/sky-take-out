package com.sky.service.impl;

import com.alibaba.fastjson.serializer.BeanContext;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车中的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //如果已经存在了
        if(list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在需要插入一条购物车数据
            //判断本次插入是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //添加的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //本次添加的是套餐
                Setmeal setmeal = setMealMapper.selectById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 显示购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingList() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> shoppingCartsList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartsList;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long userId = BaseContext.getCurrentId();
        if (dishId == null) {
            //删除的是套餐 通过套餐id查询购物车中该套餐信息
            ShoppingCart shoppingCartSetmeal = shoppingCartMapper.getBySetmealId(setmealId,userId);
            //防止空指针异常
            if (shoppingCartSetmeal == null ){
                return;
            }
            Integer setmealNumber = shoppingCartSetmeal.getNumber();
            if (setmealNumber > 1) {
                shoppingCartSetmeal.setNumber(setmealNumber-1);
                shoppingCartMapper.update(shoppingCartSetmeal);
            } else {
                //删除整个套餐
                shoppingCartMapper.delete(shoppingCartSetmeal);
            }
        } else {
            //删除的是菜品
            ShoppingCart shoppingCartDish = shoppingCartMapper.getByDishId(dishId,userId);
            //防止空指针异常
            if (shoppingCartDish == null ){
                return;
            }
            Integer dishNumber = shoppingCartDish.getNumber();
            if (dishNumber > 1) {
                shoppingCartDish.setNumber(dishNumber-1);
                shoppingCartMapper.update(shoppingCartDish);
            } else {
                //删除整个菜品
                shoppingCartMapper.delete(shoppingCartDish);
            }
        }
    }
}
