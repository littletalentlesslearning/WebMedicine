package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.entity.User;
import com.example.springboot_shixun.mapper.UserMapper;
import com.example.springboot_shixun.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
