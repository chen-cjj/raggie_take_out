package com.jun.reggie.controller;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jun.reggie.common.R;
import com.jun.reggie.entity.Category;
import com.jun.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info(category.toString());
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 获取菜品分类page
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize) {
        // 设置mp的分页对象
        Page<Category> pageInfo = new Page(page,pageSize);
        // 添加条件
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("sort");
        // 查询结果放入pageInfo对象中，并返回给浏览器
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(long ids) {
        log.info("删除菜单id: {}",ids);
        categoryService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info(category.toString());
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 获取菜品分类1或者套餐分类2
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getCategory(Category category) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(category.getType() != null,"type", category.getType());
        queryWrapper.orderByAsc("sort").orderByDesc("update_time");
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
