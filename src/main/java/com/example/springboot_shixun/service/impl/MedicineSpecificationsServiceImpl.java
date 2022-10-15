package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.entity.MedicineSpecifications;
import com.example.springboot_shixun.mapper.MedicineSpecificationsMapper;
import com.example.springboot_shixun.service.MedicineSpecificationsService;
import org.springframework.stereotype.Service;

@Service
public class MedicineSpecificationsServiceImpl extends ServiceImpl<MedicineSpecificationsMapper, MedicineSpecifications>implements MedicineSpecificationsService {
}
