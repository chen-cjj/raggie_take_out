package com.jun.reggie.mapper;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jun.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
