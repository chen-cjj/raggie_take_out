package com.jun.reggie.common;
/*
 * @author cjj
 * 自定义异常处理
 * */
public class CustomException extends RuntimeException {
    public CustomException(String mes) {
        super(mes);
    }
}
