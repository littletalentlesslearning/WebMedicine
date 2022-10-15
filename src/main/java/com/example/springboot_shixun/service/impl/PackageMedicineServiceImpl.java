package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.entity.PackageMedicine;
import com.example.springboot_shixun.mapper.PackageMedicineMapper;
import com.example.springboot_shixun.service.PackageMedicineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PackageMedicineServiceImpl extends ServiceImpl<PackageMedicineMapper, PackageMedicine> implements PackageMedicineService {
}
