package com.jun.reggie.service;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.IService;
import com.jun.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void delete(long id);
}
