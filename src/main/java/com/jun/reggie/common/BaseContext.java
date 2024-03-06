package com.jun.reggie.common;
/*
 * @author cjj
 * 基于ThreadLocal封装的工具类
 * */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setId(Long id) {
        threadLocal.set(id);
    }
    public static Long getId() {
        return threadLocal.get();
    }
}
