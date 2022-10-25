package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.dto.PackageDto;
import com.example.springboot_shixun.entity.Category;
import com.example.springboot_shixun.entity.Medicine;
import com.example.springboot_shixun.entity.Package;
import com.example.springboot_shixun.entity.PackageMedicine;
import com.example.springboot_shixun.service.CategoryService;
import com.example.springboot_shixun.service.PackageMedicineService;
import com.example.springboot_shixun.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/package")
@Slf4j
public class PackageController {
    @Autowired
   private PackageService packageService;

    @Autowired
    private PackageMedicineService packageMedicineService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody PackageDto packageDto){
        log.info(packageDto.toString());
        packageService.saveWithMedicine(packageDto);
        return  Result.success("插入成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Package> page1 = new Page<>(page, pageSize);
        Page<PackageDto> packageDtoPage = new Page<>(page,pageSize);

        //条件构造器，可能会用到name,<>记得是package实体类
        LambdaQueryWrapper<Package> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Package::getName,name);
        //查找两张表的信息，
       packageService.page(page1,queryWrapper);

       //通过categoryid查询分类名称
        //拷贝数据到dto，是beanUtils
        BeanUtils.copyProperties(page1,packageDtoPage,"records");
        List<Package> records = page1.getRecords();
       List<PackageDto> list = records.stream().map((item) -> {
           PackageDto packageDto = new PackageDto();

           Long categoryId = item.getCategoryId();
           Category category = categoryService.getById(categoryId);
           if (category != null){
               String categoryName = category.getName();
               packageDto.setCategoryName(categoryName);
           }
           BeanUtils.copyProperties(item,packageDto);
           return packageDto;
       }).collect(Collectors.toList());

       packageDtoPage.setRecords(list);
        return Result.success(packageDtoPage);
    }

    /**
     * 删除套餐
     * 可直接用Long【】数据接收，实体类就是long，也可以string接收split切割
     * list集合，可接受多个数组，id都是long型,
     * 传过来的数据本身是数组形式所以加不加注解无所谓，但list是列表所以要加注解@RequestParam可正确接受到
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){

        packageService.removeWithMedicine(ids);
        return Result.success("套餐数据删除成功");
    }



    @PostMapping("/status")
    public Result<String> update(int status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Package> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Package::getId,ids);


        return Result.success("套餐信息修改成功");
    }

    /**
     * 通过套餐分类显示所有套餐
     * url传数据不是json
     * @param package1
     * @return
     */
    @GetMapping("/list")
    public Result<List<Package>> list(Package package1){
        Long categoryId = package1.getCategoryId();
        LambdaQueryWrapper<Package> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Package::getCategoryId,categoryId);
        //添加条件，查询状态为1（起售状态）
        queryWrapper.eq(Package::getStatus,1);
        queryWrapper.orderByDesc(Package::getUpdateTime);
        List<Package> list = packageService.list(queryWrapper);




        return Result.success(list);
    }
}
