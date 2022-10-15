package com.example.springboot_shixun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_shixun.entity.Category;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 药品品及套餐分类 Mapper 接口
 * </p>
 *
 * @author ff
 * @since 2022-10-07
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
