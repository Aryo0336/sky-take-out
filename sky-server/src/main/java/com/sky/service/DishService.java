package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void save(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO queryById(long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> queryByCategoryId(long categoryId);

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 修改菜品状态
     * @param id
     * @param status
     */
    void switchDishStatus(long id, Integer status);

    /**
     * 批量删除菜品
     * @param dishIds
     */
    void delete(List<Long> dishIds);
}
