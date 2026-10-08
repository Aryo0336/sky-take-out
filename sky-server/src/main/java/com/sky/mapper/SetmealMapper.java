package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
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
    @AutoFill(value = OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     * 新增套餐所包含的菜品信息
     * @param setmealId
     * @param setmealDishes
     */
    void saveSetmealDish(long setmealId, List<SetmealDish> setmealDishes);

    /**
     * 套餐分页查询(不含菜品信息)
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐基本信息
     * @param id
     * @return
     */
    SetmealVO queryById(long id);

    /**
     * 根据套餐id查询套餐所包含的菜品
     * @param setmealId
     * @return
     */
    List<SetmealDish> querySetmealDish(long setmealId);

    /**
     * 更新套餐基本信息(不含套餐所包含的菜品)
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 删除套餐相关菜品信息
     * @param setmealIds
     */
    void deleteSetmealDish(List<Long> setmealIds);

    /**
     * 批量删除套餐基本信息
     * @param ids
     */
    void delete(List<Long> ids);
}
