package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.common.CustomException;
import com.example.springboot_shixun.entity.Category;
import com.example.springboot_shixun.entity.Medicine;
import com.example.springboot_shixun.entity.Package;
import com.example.springboot_shixun.mapper.CategoryMapper;
import com.example.springboot_shixun.service.CategoryService;
import com.example.springboot_shixun.service.MedicineService;
import com.example.springboot_shixun.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 有异常没抛出，全局异常捕捉不到，另一个文件是全局异常处理器
 * 这是我们自己添加的逻辑删除异常，来交给自己造的异常进行捕捉
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private MedicineService medicineService;

    @Autowired
    private PackageService packageService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Medicine> medicinelambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询,eq等值查询,这里查出记录，下面做统计
        medicinelambdaQueryWrapper.eq(Medicine::getCategoryId,id);
        int count1 = medicineService.count(medicinelambdaQueryWrapper);

        //查询当前分类是否关联了药品，如果已经关联，抛出一个业务异常
        if (count1 > 0){
            //已经关联药品。抛出一个业务异常
            throw new CustomException("当前分类下关联了药品，不能删除");
        }
        //查询当前分类是否关联了药品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Package> packageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        packageLambdaQueryWrapper.eq(Package::getCategoryId, id);
        int count2 = packageService.count(packageLambdaQueryWrapper);
        if (count2 > 0){
            //已经关联套餐。抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
