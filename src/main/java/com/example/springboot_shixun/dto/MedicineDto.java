package com.example.springboot_shixun.dto;

import com.example.springboot_shixun.entity.Medicine;
import com.example.springboot_shixun.entity.MedicineSpecifications;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * dto用于封装页面提交的数据，之前写的接收直接用实体是因为内容和实体相符，这次页面会有除了medicine以外的规格属性，实体填充不了所以需要dto封装
 * dto全称Data Transfer Object即数据传输对象，一般用于展示层与服务层之间的数据传输
 * dto最大作用是用于多表查询，简化了操作
 * 别人的理解：一、vo是后端往前端发送的数据封装成对象传输，dto是前端给后端发送的数据封装成对象，跟数据库对应的对象叫do
 *           二、dto字段是对应前端页面，二vo是转换实体类到dto或者dto转换到实体类的一个方法
 *            三vo包下面放一个req文件夹和resp文件夹分别对应参数和返回值封装
 * 原实体类保存key：value名字 dto实体类保存value名字对应的json数组
 */
@Data
public class MedicineDto extends Medicine {
    //下面都是扩展的属性，flavors目的就是接收页面提交的flavors属性，比如里面的name和value就会封装到MedicineSpecifications里的，而可能不只一个数组类型得list
    private List<MedicineSpecifications> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
