package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.springboot_shixun.common.BaseContext;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.entity.AddressBook;
import com.example.springboot_shixun.entity.User;
import com.example.springboot_shixun.service.AddressBookService;
import com.example.springboot_shixun.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;
    @GetMapping("list")
    public Result<AddressBook> list(){
        return null;
    }

    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook){
        log.info(addressBook.toString());
        //传过来的值少了userid，通过手机查找(逻辑优化需补充,userid从session拿，手机号不一定要号主一样的)
       //threadlocal就是获取session中的id的,之前已经封装好
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success("添加地址成功");
    }

    /**
     * 设置默认地址
     * 一个用户id只能有一个默认地址
     * 写的路径不带/，idea会自动加上
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public Result<AddressBook> setdefault(@RequestBody AddressBook addressBook){
        //条件构造器，update不是query这是两个
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //将id下的所有地址变成0，就没有默认地址
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }
}
