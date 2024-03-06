package com.jun.reggie.common;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 自定义元数据对象处理器
 * 把插入和更新操作的公共代码提取出来
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 当进行插入操作，自动执行这个方法
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入自动填充");
        metaObject.setValue("createUser", BaseContext.getId());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
    /**
     * 当进行更新操作，自动执行这个方法
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新自动填充");
        metaObject.setValue("updateUser", BaseContext.getId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
