package com.jun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jun.reggie.common.R;
import com.jun.reggie.dto.DishDto;
import com.jun.reggie.dto.SetmealDto;
import com.jun.reggie.entity.*;
import com.jun.reggie.service.SetmealDishService;
import com.jun.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * @author cjj
 * */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        setmealService.addSetmeal(setmealDto);
        return R.success("套餐新增成功");
    }

    /**
     * 获取菜单分页数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        return R.success(setmealService.getSetmeal(page, pageSize, name));
    }

    /**
     * 删除菜单数据
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info(ids.toString());
        setmealService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(0);
        }
        setmealService.updateBatchById(setmeals);
        log.info(ids.toString());
        return R.success("停售成功");
    }
    /**
     * 起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> statusOpen(@RequestParam List<Long> ids) {
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(1);
        }
        setmealService.updateBatchById(setmeals);
        log.info(ids.toString());
        return R.success("起售成功");
    }

    // http://localhost:8080/setmeal/1415580119015145474
    @GetMapping("/{id}")
    public R<Setmeal> get(@PathVariable long id) {
        Setmeal setmeal = setmealService.getById(id);
        return R.success(setmeal);
    }
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId +'_'+#setmeal.status")
    public R<List<Setmeal>> getList(Setmeal setmeal) {
        // 根据categoryId查询setmeal
        Long categoryId = setmeal.getCategoryId();
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("category_id", categoryId)
                .eq("status", 1)
                .orderByDesc("update_time");
        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        return R.success(setmeals);
    }
}

