package com.example.springboot_shixun.dto;

import com.example.springboot_shixun.entity.Package;
import com.example.springboot_shixun.entity.PackageMedicine;
import lombok.Data;

import java.util.List;

@Data
public class PackageDto extends Package {

    private List<PackageMedicine>  packageMedicines;

    private String categoryName;
}
