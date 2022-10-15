package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.common.CustomException;
import com.example.springboot_shixun.dto.MedicineDto;
import com.example.springboot_shixun.entity.Medicine;
import com.example.springboot_shixun.entity.MedicineSpecifications;
import com.example.springboot_shixun.entity.Package;
import com.example.springboot_shixun.entity.PackageMedicine;
import com.example.springboot_shixun.mapper.MedicineMapper;
import com.example.springboot_shixun.service.MedicineService;
import com.example.springboot_shixun.service.MedicineSpecificationsService;
import com.example.springboot_shixun.service.PackageMedicineService;
import com.example.springboot_shixun.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {

    @Autowired
    private MedicineSpecificationsService medicineSpecificationsService;

    @Autowired
    private PackageMedicineService packageMedicineService;

    @Autowired
    private PackageService packageService;
    /**
     * x=新增药品，同时保存对应的规格
     * 多张表操作加事务注解，使这个注解生效要去application里加@EnableTransactionManagement
     * @Transactional保证事务一致性
     * @param medicineDto
     */
    @Transactional
    public void saveWithSpecifications(MedicineDto medicineDto) {
       //保存药品的基本信息到药品表medicine，直接传medicinedto是因为这个是继承medicine的
        this.save(medicineDto);

        //保存药品规格到药品表medicine_specifications,saveBatch是因为它是个集合，下面这样有问题只有name与value，而id没有封装上
        //medicineSpecificationsService.saveBatch(medicineDto.getFlavors());

        Long medicineId = medicineDto.getId();//药品id

        //药品规格
        List<MedicineSpecifications> flavors = medicineDto.getFlavors();
        //通过遍历给flavors中的id赋值上面的id，方法一f.forEach(),二是stream流
        flavors = flavors.stream().map((item) -> {
            //把集合每个元素处理一下
            item.setMedicineId(medicineId);
            return item;
            //最后有转回list
        }).collect(Collectors.toList());

        //保存药品规格到药品表medicine_specifications,saveBatch是因为它是个集合，下面这样有问题只有name与value，而id没有封装上
        medicineSpecificationsService.saveBatch(flavors);
    }

    /**
     * 根据id查询药品信息和规格信息
     * @param id
     * @return
     */
    public MedicineDto getByIdWithFlavor(Long id) {
        //查询药品信息，medicine表
        Medicine medicine = this.getById(id);

        MedicineDto medicineDto = new MedicineDto();
        BeanUtils.copyProperties(medicine,medicineDto);

        //查询规格信息，medicine_sprcifications
        //添加条件构造器
        LambdaQueryWrapper<MedicineSpecifications> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MedicineSpecifications::getMedicineId,medicine.getId());
        //因为dto里面属性是口味这里统一用flavors防止弄乱
        List<MedicineSpecifications> flavors = medicineSpecificationsService.list(queryWrapper);
        medicineDto.setFlavors(flavors);

        return medicineDto;
    }

    @Override
    @Transactional
    public void updateWithSpecifications(MedicineDto medicineDto) {
        //一、更新medicine表基本信息,因为dto是继承的直接把普通的就能修改数据了
        this.updateById(medicineDto);

        //二、清理当前药品规格的数据--medicine_specifications表的delete操作
        LambdaQueryWrapper<MedicineSpecifications> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MedicineSpecifications::getMedicineId,medicineDto.getId());

        medicineSpecificationsService.remove(queryWrapper);

        //三添加当前提交过来的规格数据-m_s的insert操作
        List<MedicineSpecifications> flavors = medicineDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            //把集合每个元素处理一下
            item.setMedicineId(medicineDto.getId());
            return item;
            //最后有转回list
        }).collect(Collectors.toList());

        medicineSpecificationsService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void deleteByid(List<Long> ids) {
        LambdaQueryWrapper<Medicine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Medicine::getId,ids);
        queryWrapper.eq(Medicine::getStatus,1);
        int count = this.count(queryWrapper);

        if (count > 0){
            throw new CustomException("药正在起售，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<MedicineSpecifications> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(ids != null,MedicineSpecifications::getMedicineId,ids);
        medicineSpecificationsService.remove(queryWrapper1);

    }

    /**
     * 删除批量个药品包含单个，优化逻辑
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public String deleteByid2(List<Long> ids) {
        //根据药品id在package表中查出哪些套餐包含该药品
        LambdaQueryWrapper<PackageMedicine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,PackageMedicine::getMedicineId,ids);
        List<PackageMedicine> packageMedicinelist = packageMedicineService.list(queryWrapper);

        //如果药品没有在套餐中且停售状态可删除
        if (packageMedicinelist.size() == 0){
            this.deleteByid(ids);
            return "药品删除成功";
        }

        //如果药品在套餐中，且在售卖那么不能删除
        //得到与删除药品有关的套餐id
        ArrayList<String> packageidList = new ArrayList<>();
        for (PackageMedicine packageMedicine : packageMedicinelist){
            String packageId = packageMedicine.getPackageId();
            packageidList.add(packageId);
        }

        //查询与要删除药品相关的套餐,为了查询售卖状态
        LambdaQueryWrapper<Package> packagewrapper = new LambdaQueryWrapper<>();
        packagewrapper.in(Package::getId,packageidList);
        List<Package> packageList = packageService.list(packagewrapper);
        for (Package package1 : packageList){
            Integer status = package1.getStatus();
            if (status == 1){
                throw new CustomException("删除的菜品中有关联在售套餐,删除失败！");
            }
        }

        //要删除的药品没有在售，可以删除
        //若status为1，下面代码不会实现。
        this.deleteByid(ids);

        return "药品删除成功";
    }
}
