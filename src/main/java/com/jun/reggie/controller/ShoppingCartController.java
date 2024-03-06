package com.jun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jun.reggie.common.R;
import com.jun.reggie.entity.ShoppingCart;
import com.jun.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author cjj
 * */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @param session
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        // 指定是哪个用户的购物车
        shoppingCart.setUserId((Long) session.getAttribute("user"));
        // 查询是否已有数据
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper
                .eq(dishId != null, "dish_id", dishId)
                .eq(setmealId != null, "setmeal_id", setmealId);
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        // 已有+1
        if(cart!=null) {
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
        } else {
            cart = shoppingCart;
            shoppingCartService.save(cart);
        }
        return R.success(cart);
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * 删除单个
     *
     * @param map
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map map) {
        Object dishId = map.get("dishId");
        Object setmealId = map.get("setmealId");
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(dishId != null, "dish_id", dishId);
        queryWrapper.eq(setmealId != null, "setmeal_id", setmealId);
        ShoppingCart shoppingCart = shoppingCartService.getOne(queryWrapper);
        Integer number = shoppingCart.getNumber();
        if(number>1) {
            shoppingCart.setNumber(number-1);
            shoppingCartService.updateById(shoppingCart);
        } else {
            shoppingCartService.remove(queryWrapper);
        }

        return R.success("移除成功");
    }

    /**
     * 删除所有
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("id");
        shoppingCartService.remove(queryWrapper);
        return R.success("全部清除");
    }
}
