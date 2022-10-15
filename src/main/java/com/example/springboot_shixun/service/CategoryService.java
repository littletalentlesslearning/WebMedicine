package com.example.springboot_shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_shixun.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
