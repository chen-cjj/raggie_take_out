package com.jun.reggie.mapper;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jun.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
