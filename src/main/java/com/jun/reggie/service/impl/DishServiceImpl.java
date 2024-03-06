package com.jun.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.Dish;
import com.jun.reggie.mapper.DishMapper;
import com.jun.reggie.service.DishService;
import org.springframework.stereotype.Service;

/*
 * @author cjj
 * */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
