package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional // 开启事务管理
    @Override
    public void save(DishDTO dishDTO) {
        // 对象属性拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(0); // 设置菜品默认状态为不启用
        // 1.添加菜品
        dishMapper.saveDish(dish);
        // 2.添加菜品口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            long dishId = dish.getId(); // 获取菜品id
            dishFlavors.forEach(df -> df.setDishId(dishId));
            dishMapper.saveFlavors(dishFlavors);
        }
    }
}
