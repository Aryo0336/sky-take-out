package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐基本信息
     * @param setmeal
     */
    void save(Setmeal setmeal);

    /**
     * 新增套餐所包含的菜品信息
     * @param setmealId
     * @param setmealDishes
     */
    void saveSetmealDish(long setmealId, List<SetmealDish> setmealDishes);
}
