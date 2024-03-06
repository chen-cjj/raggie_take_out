package com.jun.reggie.controller;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jun.reggie.common.R;
import com.jun.reggie.entity.Employee;
import com.jun.reggie.service.EmployeeService;
import com.jun.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    private R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1.把密码md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2.查询数据库，查该用户是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3.如果用户不存在
        if (emp == null) return R.error("登录失败");

        // 4.判断密码是否正确
        if (!emp.getPassword().equals(password)) return R.error("登录失败");

        // 5.判断status是否为0,被封号
        if (emp.getStatus() == 0) return R.error("账户已经锁定");

        // 5.登录成功，将用户id存入session，并且返回该用户信息
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    private R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工信息
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    private R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        // 设置默认密码为123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        // 调用方法存入数据库
        employeeService.save(employee);
        log.info("新增员工信息:{}", employee.toString());
        return R.success("新增员工成功");
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    private R<Page> page(int page,int pageSize,String name) {
        log.info("page={} pageSize={} name={}",page,pageSize,name);
        // 创建mp自带的分页对象
        Page<Employee> pageInfo = new Page(page,pageSize);
        // 设置条件
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),"username", name);
        queryWrapper.orderByDesc("update_time");
        // 查询结果放入pageInfo对象中，并返回给浏览器
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工账号状态
     * @param employee
     * @return
     */
    @PutMapping
    private R<String> update(@RequestBody Employee employee) {
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((long)request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    private R<Employee> query(@PathVariable long id) {
        log.info("查询用户信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null) {
            return R.success(employee);
        }
        return R.error("查询失败");
    }
}

