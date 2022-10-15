package com.example.springboot_shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_shixun.entity.Employee;
import com.example.springboot_shixun.mapper.EmployeeMapper;
import com.example.springboot_shixun.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
//继承父类实现父接口
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
