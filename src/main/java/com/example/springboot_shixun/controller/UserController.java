package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.entity.Employee;
import com.example.springboot_shixun.entity.User;
import com.example.springboot_shixun.service.UserService;
import com.example.springboot_shixun.utils.SMSceshi;
import com.example.springboot_shixun.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //判断是否为空
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码，使用ValidateCodeUtils.java
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);

            //调用阿里云短信服务API完成发送短信，sendMsg
            SMSceshi.sendMessage(phone,code);
            //需要将生成的验证码保存到session,手机号作为key，code为值
            session.setAttribute(phone,code);
            Result.success("手机验证码短信发送成功");
        }

        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登陆
     * 前端传递两个参数，phone，code，phone作为识别的id，code在后端进行对比，两个方式接受，一dto，二map本身就是key，value键值对
     * @param usermap
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map usermap, HttpSession session){
       log.info(usermap.toString());

         //获取手机号
        String phone =  usermap.get("phone").toString();
        //获取验证码
        String code = usermap.get("code").toString();
        //从session中获取验证码,上面是用手机号作为key,换手机号码是获得不了session
        Object sessioncode = session.getAttribute(phone);
        //验证码比对
        if (sessioncode != null && sessioncode.equals(code)) {
            //比对成功，说明登陆成功
            //判断当前用户是否为新用户，如果是新用户自动完成注册
            //条件构造器，添加查询phone
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            //使用getone，因为手机号是唯一标识
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                //状态设置不设置都行数据库有默认值
                user.setStatus(1);
                userService.save(user);
            }
            //浏览器有保存用户信息
            session.setAttribute("user",user.getId());
                return Result.success(user);

        }
        //比对失败返回error


        return Result.error("登陆失败");
    }

    /**
     * 用户退出
     * @return
     */
    @PostMapping("/loginout")
    //要操作参数。把request取出
    public Result<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().getAttribute("user");
        return Result.success("退出成功");
    }
}
