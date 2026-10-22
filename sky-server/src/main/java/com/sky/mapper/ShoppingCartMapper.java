package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 购物车新增商品
     * @param shoppingCart
     */
    void add(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改商品数量
     * @param shoppingCart
     */
    void updateNumber(ShoppingCart shoppingCart);

    /**
     * 清理购物车
     * @param shoppingCart
     */
    void clean(ShoppingCart shoppingCart);
}
