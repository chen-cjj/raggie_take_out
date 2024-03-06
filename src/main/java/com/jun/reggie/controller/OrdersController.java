package com.jun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jun.reggie.common.R;
import com.jun.reggie.entity.Orders;
import com.jun.reggie.entity.ShoppingCart;
import com.jun.reggie.service.OrdersService;
import com.jun.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/*
 * @author cjj
 * */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("success");
    }
}
