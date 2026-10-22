package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
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
    private SetmealMapper setmealMapper;

    /**
     * 购物车新增商品
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        // 对象属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 设置用户id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户id为空");
        } else {
            shoppingCart.setUserId(userId);
            // 判断购物车中是否存在该商品
            List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
            // 商品存在, 数量+1
            if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
                ShoppingCart currentDish = shoppingCarts.get(0);
                currentDish.setNumber(currentDish.getNumber() + 1);
                shoppingCartMapper.updateNumber(currentDish);
            }else {
                // 商品不存在, 新增
                shoppingCart.setNumber(1); // 设置商品初始数量为1
                Long dishId = shoppingCart.getDishId();
                if (dishId != null) {
                    // 商品为菜品, 设置其相关信息
                    DishVO dish = dishMapper.queryById(dishId);
                    shoppingCart.setName(dish.getName());
                    shoppingCart.setImage(dish.getImage());
                    shoppingCart.setAmount(dish.getPrice());
                    shoppingCart.setCreateTime(LocalDateTime.now());
                    shoppingCartMapper.add(shoppingCart);
                }else {
                    // 商品为套餐
                    Long setmealId = shoppingCart.getSetmealId();
                    SetmealVO setmeal = setmealMapper.queryById(setmealId);
                    shoppingCart.setName(setmeal.getName());
                    shoppingCart.setImage(setmeal.getImage());
                    shoppingCart.setAmount(setmeal.getPrice());
                    shoppingCart.setCreateTime(LocalDateTime.now());
                    shoppingCartMapper.add(shoppingCart);
                }
            }
        }
    }

    /**
     * 购物车减少商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 设置当前用户id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户id为空");
        }
        shoppingCart.setUserId(userId);
        // 查找指定商品
        List<ShoppingCart> dishList = shoppingCartMapper.list(shoppingCart);
        if (dishList != null && !dishList.isEmpty()) {
            ShoppingCart dish = dishList.get(0);
            // 获取当前商品数量
            Integer dishNum = dish.getNumber();
            if (dishNum != null && dishNum > 1) {
                // 商品数量大于1, 数量直接-1
                dish.setNumber(dishNum - 1);
                shoppingCartMapper.updateNumber(dish);
            }else {
                // 商品数量为1, 从购物车中删除该商品
                shoppingCartMapper.clean(dish);
            }
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户id为空");
        }
        shoppingCart.setUserId(userId);
        shoppingCartMapper.clean(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        // 设置当前用户id
        if (userId == null) {
            throw new RuntimeException("用户id为空");
        }
        shoppingCart.setUserId(userId);
        return shoppingCartMapper.list(shoppingCart);
    }
}