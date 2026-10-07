package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品, {}", dishDTO.getName());
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询,page={},pageSize={},name={},categoryId={},status={}",
                dishPageQueryDTO.getPage(),
                dishPageQueryDTO.getPageSize(),
                dishPageQueryDTO.getName(),
                dishPageQueryDTO.getCategoryId(),
                dishPageQueryDTO.getStatus());
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据菜品id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> queryById(@PathVariable long id) {
        log.info("查询菜品,id={}", id);
        DishVO dishVO = dishService.queryById(id);
        return Result.success(dishVO);
    }

    /**
     * 更新菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品信息, id={},name={},flavors={},categoryId={},price={},image={},description={}",
                dishDTO.getId(),
                dishDTO.getName(),
                dishDTO.getFlavors(),
                dishDTO.getCategoryId(),
                dishDTO.getPrice(),
                dishDTO.getImage(),
                dishDTO.getDescription());
        dishService.update(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result switchDishStatus(long id, @PathVariable Integer status) {
        log.info("修改菜品状态, id={}, status={}", id, status);
        dishService.switchDishStatus(id, status);
        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除菜品, id={}", ids.toString());
        dishService.delete(ids);
        return Result.success();
    }
}
