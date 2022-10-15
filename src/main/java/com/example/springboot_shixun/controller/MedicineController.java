package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.dto.MedicineDto;
import com.example.springboot_shixun.entity.Category;
import com.example.springboot_shixun.entity.Medicine;
import com.example.springboot_shixun.service.CategoryService;
import com.example.springboot_shixun.service.MedicineService;
import com.example.springboot_shixun.service.MedicineSpecificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品管理
 */
@RestController
@RequestMapping("/medicine")
@Slf4j
public class MedicineController {
    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineSpecificationsService medicineSpecificationsService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增药品
     * 前端提交json数据需加@requestbody转换
     * @param medicineDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody MedicineDto medicineDto){
        log.info(medicineDto.toString());

        //要操作两张表需要在medicineservice中添加方法，在两张表中插入数据
        medicineService.saveWithSpecifications(medicineDto);
        return Result.success("新增药品成功");
    }

    /**
     * 药品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){

        //分页构造器
        Page<Medicine> pageinfo = new Page<>(page, pageSize);
        Page<MedicineDto> medicineDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,模糊查询
        queryWrapper.like(name != null,Medicine::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Medicine::getUpdateTime);

        //执行分页查询
        medicineService.page(pageinfo,queryWrapper);
        //不能直接存pageinfo，因为里面存的是categoryid而页面要的是药品分类
        //return Result.success(pageinfo);

        //一、对象拷贝，通过分类id查分类表得分类名字再封装
        //这个medicineDtoPage进行的分页查询其实是查不到数据的，因为后面没有传页数和数据量，这里是先把Dish分页查询的数据copy到dto实体类当中
        //拷贝的是分页信息并非实体类信息
        //这里不需要全拷贝，分页构造器的page（）page里面的record是查出来的记录集合，所以把record忽略掉加入三个参数
        BeanUtils.copyProperties(pageinfo,medicineDtoPage,"records");

        //二、处理record
        List<Medicine> records = pageinfo.getRecords();
        //三、基于上面的record处理得到list集合,item代表medicine对象
        List<MedicineDto> list = records.stream().map((item) ->{
            MedicineDto medicineDto = new MedicineDto();

            //五将item也赋值进去
            BeanUtils.copyProperties(item,medicineDto);
            //四、分类id，先往下看
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            //防止导入数据有缺陷，导致查询某一分类找不到，如果查不到就不用设置
            if (category != null) {
                String categoryName = category.getName();
                //查询后要对dto赋值名字，因为是new其他属性为空，还需将item也赋值进去
                medicineDto.setCategoryName(categoryName);
            }

            return medicineDto;
            //最后需要将medicinedto收集起来
        }).collect(Collectors.toList());
        //写在上面了
        //List<MedicineDto> list = null;

        //再将记录封装到page里
        medicineDtoPage.setRecords(list);
        return Result.success(medicineDtoPage);
    }



    /**
     * 根据id查询药品和对应规格
     * id是在请求路径里得用@PathVariable，回显是有flavour属性的得用dto
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MedicineDto> get(@PathVariable Long id){
        MedicineDto medicineDto = medicineService.getByIdWithFlavor(id);

        return Result.success(medicineDto);
    }

    /**
     * 修改药品
     * 前端提交json数据需加@requestbody转换
     * @param medicineDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody MedicineDto medicineDto){
        log.info(medicineDto.toString());

        //要操作两张表需要在medicineservice中添加方法，在两张表中修改数据
        medicineService.updateWithSpecifications(medicineDto);
        return Result.success("修改药品成功");
    }

    /**
     * 根据药品类别或药品名称查询药品
     * get属性路径请求，若是直接一串id用path，地址为“/{id}”，但要是有具体属性一个或几个则封装实体
     * 这里可以不用@requestbody，因为直接在路径上获取
     * @param medicine
     * @return
     */
    @GetMapping("/list")
    public Result<List<Medicine>> list( Medicine medicine){
        //先根据分类id查询medicine表
        //条件构造器
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件,先判断id是否为空
        queryWrapper.eq(medicine.getCategoryId() != null,Medicine::getCategoryId,medicine.getCategoryId());
        //添加条件，查询状态为1（起售状态）
        queryWrapper.eq(Medicine::getStatus,1);
            //添加排序条件
            queryWrapper.orderByAsc(Medicine::getSort).orderByDesc(Medicine::getUpdateTime);

        //根据药品名称查询表
        queryWrapper.like(medicine.getName() != null,Medicine::getName,medicine.getName());

        //查询,返回的是一个集合
        List<Medicine> list = medicineService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     *对药品进行批量起售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Medicine::getId,ids);
        List<Medicine> list = medicineService.list(queryWrapper);

        for (Medicine medicine : list) {
            if (medicine != null) {
                medicine.setStatus(status);
                medicineService.updateById(medicine);
            }
        }
        return Result.success("药品售卖状态修改成功");
    }

    /**
     * 批量删除或删除单个药品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") List<Long> ids){
        //优化的删除
        String msg = medicineService.deleteByid2(ids);
        return Result.success(msg);
    }

}
