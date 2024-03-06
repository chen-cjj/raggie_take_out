package com.jun.reggie.service;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jun.reggie.dto.SetmealDto;
import com.jun.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    // 添加套餐功能
    public void addSetmeal(SetmealDto setmealDto);
    // 分页查询套餐
    public Page<SetmealDto> getSetmeal(int page, int pageSize, String name);
    // 删除菜单
    public void delete(List<Long> ids);
}
