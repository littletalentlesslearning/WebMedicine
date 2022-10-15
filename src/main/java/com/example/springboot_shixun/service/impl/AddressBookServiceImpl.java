package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.entity.AddressBook;
import com.example.springboot_shixun.mapper.AddressBookMapper;
import com.example.springboot_shixun.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
