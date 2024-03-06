package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.common.CustomException;
import com.jun.reggie.entity.Category;
import com.jun.reggie.mapper.CategoryMapper;
import com.jun.reggie.service.CategoryService;
import com.jun.reggie.service.DishService;
import com.jun.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    // 把Setmeal和Dish的service方法引入，便于调用的mp方法，查询是否有与删除分类id相同
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 自定义的删除分类方法
     * 需要检验该分类是否有内容
     */
    @Override
    public void delete(long id) {
        // 检查dish和setmeal是否有相同id
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("category_id", id);

        // 菜品有内容
        if(dishService.count(queryWrapper)>0) {
            // 抛出异常
            throw new CustomException("菜品已有内容，无法删除分类");
        }

        // 套餐有内容
        if(setmealService.count(queryWrapper)>0) {
            // 抛出异常
            throw new CustomException("套餐已有内容，无法删除分类");
        }
        // 可以删除
        super.removeById(id);
    }
}
