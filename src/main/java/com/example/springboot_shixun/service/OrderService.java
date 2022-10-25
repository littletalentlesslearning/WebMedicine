package com.example.springboot_shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_shixun.entity.Orders;

public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
