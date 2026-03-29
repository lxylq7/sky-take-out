package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart cart);

    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time)" +
            " values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clean(Long userId);

    /**
     * 通过套餐id查询购物车中该套餐信息
     * @param setmealId
     * @return
     */
    @Select("select * from shopping_cart where setmeal_id = #{setmealId} and user_id = #{userId}")
    ShoppingCart getBySetmealId(Long setmealId,Long userId);

    /**
     * 删除购物车中该套餐
     * @param
     */
    void delete(ShoppingCart shoppingCart);

    /**
     * 通过菜品id查询购物车中该菜品信息
     * @param dishId
     * @return
     */
    @Select("select * from shopping_cart where dish_id = #{dishId} and user_id = #{userId}")
    ShoppingCart getByDishId(Long dishId,Long userId);

    /**
     * 更新购物车中该菜品数量
     * @param shoppingCartDish
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart shoppingCartDish);

    /**
     * 删除购物车中该用户所有数据
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
