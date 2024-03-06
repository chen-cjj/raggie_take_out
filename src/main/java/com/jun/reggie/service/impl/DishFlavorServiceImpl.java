package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.DishFlavor;
import com.jun.reggie.mapper.DishFlavorMapper;
import com.jun.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
