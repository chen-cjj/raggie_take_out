package com.jun.reggie.filter;
/*
 * @author cjj
 * */

import com.alibaba.fastjson.JSON;
import com.jun.reggie.common.BaseContext;
import com.jun.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 判断用户访问的页面是否需要登录
    // 路径匹配器，支持通配符
    public static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        // 2、定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        // 3、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 4、如果不需要处理，则直接放行
        if (check) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 5.1、判断后台用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null) {
            // 设置thread域值
            BaseContext.setId((Long)request.getSession().getAttribute("employee"));
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 5.2、判断前端用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user")!=null) {
            // 设置thread域值
            BaseContext.setId((Long)request.getSession().getAttribute("user"));
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        // 6、如果未登录则返回未登录结果（前端有一个响应拦截器，拦截到错误信息就跳转页面）
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }


    /**
     * 判断是否需要过滤
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = pathMatcher.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }
}
