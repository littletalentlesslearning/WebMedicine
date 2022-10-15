package com.example.springboot_shixun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_shixun.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//basemapper会自动生成增删改查
public interface EmployeeMapper extends BaseMapper<Employee> {
}
