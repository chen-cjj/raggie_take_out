package com.jun.reggie.service;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.IService;
import com.jun.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
