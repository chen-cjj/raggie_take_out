package com.jun.reggie.mapper;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jun.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
