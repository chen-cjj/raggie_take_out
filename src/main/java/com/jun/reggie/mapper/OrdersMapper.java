package com.jun.reggie.mapper;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jun.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
