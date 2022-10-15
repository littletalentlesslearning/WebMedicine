package com.example.springboot_shixun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_shixun.entity.Package;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PackageMapper extends BaseMapper<Package> {
}
