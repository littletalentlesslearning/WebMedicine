package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.common.CustomException;
import com.example.springboot_shixun.dto.PackageDto;
import com.example.springboot_shixun.entity.Package;
import com.example.springboot_shixun.entity.PackageMedicine;
import com.example.springboot_shixun.mapper.PackageMapper;
import com.example.springboot_shixun.service.PackageMedicineService;
import com.example.springboot_shixun.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackageServiceImpl extends ServiceImpl<PackageMapper, Package> implements PackageService {

    @Autowired
    private PackageMedicineService packageMedicineService;

    /**
     * 根据提交的dto对套餐和套餐药品表进行插入
     * @param packageDto
     */
    @Override
    public void saveWithMedicine(PackageDto packageDto) {
        //对package进行插入操作
        this.save(packageDto);

        //获取套餐id
        Long packageDtoId = packageDto.getId();
        //获取套餐内的药品信息插入套餐药品表
        List<PackageMedicine> packageMedicines = packageDto.getPackageMedicines();
        packageMedicines = packageMedicines.stream().map((item) ->{
            item.setPackageId(String.valueOf(packageDtoId));
            return item;
        }).collect(Collectors.toList());

        packageMedicineService.saveBatch(packageMedicines);
    }


    //删除套餐，同时删除与药品的关联数据
    @Transactional
    @Override
    public void removeWithMedicine(List<Long> ids) {
        //一、查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Package> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Package::getId,ids);
        queryWrapper.eq(Package::getStatus,1);

        int count = this.count(queryWrapper);
        //二、
        if (count > 0){
            //如果不能删除抛出业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据--package
        this.removeByIds(ids);

        //删除关系表中数据---packagemedicine
        LambdaQueryWrapper<PackageMedicine> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(PackageMedicine::getPackageId,ids);

        packageMedicineService.remove(queryWrapper1);
    }
}
