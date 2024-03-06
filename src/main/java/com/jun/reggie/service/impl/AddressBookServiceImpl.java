package com.jun.reggie.service.impl;
/*
 * @author cjj
 * */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.reggie.entity.AddressBook;
import com.jun.reggie.mapper.AddressBookMapper;
import com.jun.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
