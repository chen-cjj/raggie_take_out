package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.Employee;
import com.jun.reggie.mapper.EmployeeMapper;
import com.jun.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
