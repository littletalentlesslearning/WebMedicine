package com.example.springboot_shixun.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * meta元数据
 * MyMetaObjecthandler是不能获得session对象的，但用Autowrite把HttpServletRequest request封装进去可以获得session
 * 使用ThreadLocal获得session
 * 客户端发送的每个http请求,对应的在服务端都会分配一个新的线程来处理，处理过程中下面类中的方法都属于同一个线程：
 * loginCheckFilter的doFilter方法，EmployeeController的update方法，MyMetaObjectHandler的updateFill方法
 * 分页查询也会调用loginCheckFilter，这是另一个线程了
 * 需要在实体类上属性上加入@TableField注解，指定自动填充的策略
 */
//放入ioc容器中
@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        //设置值，属性名
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        //先封装basecontexr的工具类，在loginCheckFilte的4设置set,在同一文件夹里面不用导
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        //获得现成id
        Long id = Thread.currentThread().getId();
        log.info("线程id为,{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
