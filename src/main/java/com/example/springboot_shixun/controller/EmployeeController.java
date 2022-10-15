package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.entity.Employee;
import com.example.springboot_shixun.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Slf4j
//员工index前端可能是restful风格
//@RestController包含了resposebody注解
@RestController
@RequestMapping("/employee")//写的是请求路径
public class EmployeeController {
    //可以注入实现类，平常用的是面向接口编程，运用了spring的动态代理，就相当于多态
    //这里是分为几层
    //这个是byType，也可以用@Resource按名称装填
    @Autowired
    private EmployeeService employeeService;

    /**
     * JSON，全称是 JavaScript Object Notation，即 JavaScript对象标记法。js文件就是JavaScript，当中的写法就是json的写法传过来的数据就是json
     * get不安全
     * 因为前端发射的请求是post,请求路径8080/employee/login，上面已写补所需路径
     * 前端会发送账号密码是json格式接收需加@RequestBody封装成employee对象
     * 登陆成功后需将employee存到seesion，后面会用redis
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //1、将页面提交的密码password进行md5加密处理,md5是哈希函数
        String password = employee.getPassword();
        //对pass处理再赋给自己
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //进行等值对比
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //数据库已经对username做了唯一的设置 ，既然唯一就可以调用getine方法查出唯一数据再封装
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null){
            return Result.error("登陆失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        //没加盐只加密可以直接比
        if (!emp.getPassword().equals(password)){
            return Result.error("登陆失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return Result.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    /**
     * 员工退出
     * @return
     */
    @PostMapping("/logout")
    //要操作参数。把request取出
    public Result<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().getAttribute("employee");
        return Result.success("退出成功");
    }
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理是为了与数据库保存一致，前端密码也有处理，设置为数组，
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //公共字段统一在MyMetaObjecthandler。java里写
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登陆用户id,getAttribute统一返回object类型,绑定谁创建的
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        //id是调用yml里的雪花算法,Iservice是有写好增删改查的
        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * 用mybatisplus通过的page是有前端所需的records和total
     *total总计赋值给前端的counts就是共几条数据，records查出来的记录集合赋值给tableData
     * 在list中的el-pagination的page-sizes="[10, 20, 30, 40]，代表下拉框的10条/1页，20。。,这里的逗号是英文的
     * 在list中的vue下的data（）可以改pageSize显示几条
     * 前端修改后页面不显示，可以清除缓存，ctrl+f5，不行就手动清除，就可以了
     * 前端修改后写在地址不是json数据是Integer，名字匹配上自动封装，基本数据类型可以直接接收，mvc会自动转换，注解是json转换的包提供的，这里不需要转换
     * 在控制器方法的形参位置，“设置和请求参数同名的形参”，当浏览器发送请求，匹配到请求映射时，在DispatcherServlter中就会将请求参数赋值给相应的形餐
     * requestbody是加在对象上，如果是json格式就加，此处如果是page对象就加
     * pagesize是一页显示几条数据，name是搜索栏里填的，page页数第几页
     * @param page
     * @param pageSize
     * @return
     */

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageinfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件，相似度查询,如果name为空则条件不成立，成立后根据name进行查询：：
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询,提供分页查询方法
        employeeService.page(pageinfo,queryWrapper);
        return Result.success(pageinfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    //这里会获得查询的数组,不会与postmapper冲突
    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        //获得现成id
        Long id = Thread.currentThread().getId();
        log.info("线程id为,{}",id);

        //需要通过id获取对象，即上面再加HttpServletRequest，employee有传递修改后的值
       //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        //js对id丢失了精度对long型数据只能保证到前16位，导致提交id与数据库id不一致
        //可以在服务端给页面响应json数据时进行处理，将long型数据统一转为string字符串
        //如果数据库形成自增大量用户同时添加可能导致id碰撞
        return Result.success("员工信息修改成功");
    }

    /**
     * 根据员工id查询信息
     * @param id
     * @return
     */
    //地址栏传过来的id,@PathVariable路径变量
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        //另一个管理员删了，这里没刷新就可能没有
        return Result.error("没有查询到对应员工信息");
    }
}
