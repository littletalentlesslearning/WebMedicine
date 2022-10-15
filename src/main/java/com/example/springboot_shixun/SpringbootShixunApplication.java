package com.example.springboot_shixun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//输出日志
@Slf4j
@SpringBootApplication
@ServletComponentScan
//开启事务注解的支持，如@Transactional
@EnableTransactionManagement
public class SpringbootShixunApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootShixunApplication.class, args);
        log.info("启动成功");
    }

}
