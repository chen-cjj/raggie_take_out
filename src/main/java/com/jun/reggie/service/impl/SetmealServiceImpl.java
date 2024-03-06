package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.common.CustomException;
import com.jun.reggie.dto.SetmealDto;
import com.jun.reggie.entity.Category;
import com.jun.reggie.entity.Setmeal;
import com.jun.reggie.entity.SetmealDish;
import com.jun.reggie.mapper.SetmealMapper;
import com.jun.reggie.service.CategoryService;
import com.jun.reggie.service.SetmealDishService;
import com.jun.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    // 添加套餐功能
    @Override
    public void addSetmeal(SetmealDto setmealDto) {
        this.save(setmealDto);
        // setmeal的id赋值给setmealDish的setmeal_id
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }

    // 分页查询套餐
    @Override
    public Page<SetmealDto> getSetmeal(int page,int pageSize,String name) {
        // 查询Setmeal的数据
        Page<Setmeal> pageSetmeal = new Page(page,pageSize);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .like(name!=null, "name", name)
                .orderByDesc("update_time");
        setmealService.page(pageSetmeal,queryWrapper);
        // copy给SetmealDto
        Page<SetmealDto> pageSetmealDto = new Page(page,pageSize);
        BeanUtils.copyProperties(pageSetmeal, pageSetmealDto,"records");
        List<Setmeal> setmeals = pageSetmeal.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal record : setmeals) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);
            // 通过category_id查询categoryName
            Category category = categoryService.getById(record.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            setmealDtos.add(setmealDto);
        }
        pageSetmealDto.setRecords(setmealDtos);
        return pageSetmealDto;
    }

    // 删除套餐
    @Override
    public void delete(List<Long> ids) {
        // 1.删除套餐表
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .in("id", ids)
                .eq("status", 1);
        int count = this.count(queryWrapper);
        // 在售商品不能删除
        if(count>0) throw new CustomException("当前套餐在售，不能删除");
        this.removeByIds(ids);
        // 2.删除套餐关联菜品表
        QueryWrapper<SetmealDish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.in("setmeal_id",ids);
        setmealDishService.remove(dishQueryWrapper);
    }
}
