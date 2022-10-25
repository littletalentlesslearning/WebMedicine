package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.springboot_shixun.entity.ShoppingCart;
import com.example.springboot_shixun.mapper.ShoppingCartMapper;
import com.example.springboot_shixun.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
