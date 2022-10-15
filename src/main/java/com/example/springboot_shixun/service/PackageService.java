package com.example.springboot_shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_shixun.dto.PackageDto;
import com.example.springboot_shixun.entity.Package;

import java.util.List;

public interface PackageService extends IService<Package> {
    //新增套餐需要同时对两张表进行操作，即package与package_medicine
    public void saveWithMedicine(PackageDto packageDto);

    //删除套餐，同时删除与药品的关联数据
    public void removeWithMedicine(List<Long> ids);
}
