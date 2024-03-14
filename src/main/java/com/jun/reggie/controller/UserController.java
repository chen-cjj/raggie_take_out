package com.jun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jun.reggie.common.R;
import com.jun.reggie.entity.User;
import com.jun.reggie.service.UserService;
import com.jun.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * @author cjj
 * */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        // 随机生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        // 调用阿里云短信服务api
        log.info("验证码为:{}",code);
        // 保存在session中
//        session.setAttribute(phone, code);
        // 保存在redis中，设置5分钟过期
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
        return R.success("信息发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map, HttpServletRequest request, HttpSession session) {
        // 1.验证码的校验
        String phone = map.get("phone");
        String code = map.get("code");
        // 从redis中获取验证码
        // 验证码正确
        if(redisTemplate.opsForValue().get(phone).equals(code)) {
            // 2.如果数据库有这个账号就直接登录
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            User user = userService.getOne(queryWrapper);
            if(user==null) {
                // 3.如果没有就创建
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            log.info(user.toString());
            request.getSession().setAttribute("user", user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        // 验证码错误
        return R.error("验证码错误，请重试");
    }
}
