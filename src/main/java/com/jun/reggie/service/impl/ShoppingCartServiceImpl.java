package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.ShoppingCart;
import com.jun.reggie.mapper.ShoppingCartMapper;
import com.jun.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
