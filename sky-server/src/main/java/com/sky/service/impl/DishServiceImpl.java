package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 开启分页查询(只分页菜品, 保证total正确)
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> records = dishMapper.pageQuery(dishPageQueryDTO);
        long total = records.getTotal();

        // 批量查询当前页菜品的口味并填充, 避免一对多连表导致分页不准/数据重复
        if (records != null && records.size() > 0) {
            List<Long> dishIds = records.stream().map(DishVO::getId).collect(Collectors.toList());
            List<DishFlavor> flavors = dishMapper.getFlavorsByDishIds(dishIds);
            Map<Long, List<DishFlavor>> flavorMap = flavors.stream()
                    .collect(Collectors.groupingBy(DishFlavor::getDishId));
            records.forEach(d -> d.setFlavors(flavorMap.getOrDefault(d.getId(), Collections.emptyList())));
        }
        return new PageResult(total, records);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO queryById(long id) {
        return dishMapper.queryById(id);
    }

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        // 对象属性拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 1.更新菜品信息(不含口味)
        dishMapper.update(dish);
        // 2.更新菜品口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            dishMapper.updateFlavors(flavors);
        }
    }

    /**
     * 修改菜品状态
     * @param id
     * @param status
     */
    @Override
    public void switchDishStatus(long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }
}