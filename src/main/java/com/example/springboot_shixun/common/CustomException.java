package com.example.springboot_shixun.common;

/**
 * 自定义业务异常
 * 继承运行时异常
 */
public class CustomException extends RuntimeException{
    //把异常信息传进来
    public CustomException(String message) {
        super(message);
    }
}
