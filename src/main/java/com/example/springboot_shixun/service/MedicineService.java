package com.example.springboot_shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_shixun.dto.MedicineDto;
import com.example.springboot_shixun.entity.Medicine;

import java.util.List;

public interface MedicineService extends IService<Medicine> {

    //新增药品，同时插入药品对应的规格，需要操作两张表，medicine与medicine_specifications
    public  void saveWithSpecifications(MedicineDto medicineDto);

    //根据id查询药品信息和规格信息
    public MedicineDto getByIdWithFlavor(Long id);

    //修改药品，两个表
    public void updateWithSpecifications(MedicineDto medicineDto);

    //删除批量个药品包含单个
   public void deleteByid(List<Long> ids);

    //删除批量个药品包含单个，优化逻辑
    public String deleteByid2(List<Long> ids);
}
