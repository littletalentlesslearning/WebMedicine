package com.example.springboot_shixun.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * aop通知
 * try，catch得写很多次太麻烦
 *
 * 与CustomException文件区别，这个是捕获异常而custom是创建异常
 */
//通知，指定拦截哪些注解
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * 处理括号里的SQLIntegr。。。异常
     * ExceptionHandler捕获异常
     * @return
     */

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        //错误包含
        if (ex.getMessage().contains("Duplicate entry")){
            //根据空格来分隔，形成数组对象
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return Result.error(msg);
        }
        return Result.error("未知异常");
    }

    /**
     * 异常处理方法
     * 处理括号里的SQLIntegr。。。异常
     * @return
     */

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return Result.error(ex.getMessage());
    }
}
