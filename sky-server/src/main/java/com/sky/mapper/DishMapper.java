package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品, 主键返回赋值给dish的id属性
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void saveDish(Dish dish);

    /**
     * 新增菜品口味
     * @param dishFlavors
     */
    void saveFlavors(List<DishFlavor> dishFlavors);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量根据菜品id查询口味
     * @param dishIds
     * @return
     */
    List<DishFlavor> getFlavorsByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询
     * @param id
     * @return
     */
    DishVO queryById(long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from sky_take_out.dish where category_id = #{categoryId}")
    List<Dish> queryByCategoryId(long categoryId);

    /**
     * 更新菜品信息(不含口味)
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 批量删除菜品口味信息
     * @param dishIds
     */
    void deleteDishFlavors(List<Long> dishIds);

    /**
     * 批量删除菜品(不含口味)
     * @param dishIds
     */
    void delete(List<Long> dishIds);
}