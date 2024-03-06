package com.jun.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.User;
import com.jun.reggie.mapper.UserMapper;
import com.jun.reggie.service.UserService;
import org.springframework.stereotype.Service;

/*
 * @author cjj
 * */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
