package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.springboot_shixun.entity.OrderDetail;
import com.example.springboot_shixun.mapper.OrderDetailMapper;
import com.example.springboot_shixun.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}