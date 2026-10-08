package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void save(SetmealDTO setmealDTO) {
        // 对象属性拷贝
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 1.新增套餐基本信息
        setmealMapper.save(setmeal);
        // 2.新增套餐所包含的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealMapper.saveSetmealDish(setmeal.getId(), setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 执行分页查询
        Page<SetmealVO> records = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(records.getTotal(), records.getResult());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO queryById(long id) {
        // 1.查询套餐基本信息
        SetmealVO setmealVO = setmealMapper.queryById(id);
        // 2.查询套餐所包含的菜品
        List<SetmealDish> setmealDishes = setmealMapper.querySetmealDish(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 切换套餐状态
     * @param id
     * @param status
     */
    @Override
    public void switchStatus(long id, Integer status) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 更新套餐信息
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        // 对象属性拷贝
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 1.更新套餐基本信息
        setmealMapper.update(setmeal);
        // 2.更新套餐菜品信息
        long setmealId = setmeal.getId();
        setmealMapper.deleteSetmealDish(List.of(setmealId)); // 删除套餐相关菜品
        setmealMapper.saveSetmealDish(setmealId, setmealDTO.getSetmealDishes());
    }

    /**
     * 批量删除taocan
     * @param ids
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        // 1.批量删除套餐基本信息
        setmealMapper.delete(ids);
        // 2.批量删除套餐所包含的菜品信息
        setmealMapper.deleteSetmealDish(ids);
    }
}
