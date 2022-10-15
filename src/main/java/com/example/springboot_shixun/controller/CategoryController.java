package com.example.springboot_shixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot_shixun.common.Result;
import com.example.springboot_shixun.entity.Category;
import com.example.springboot_shixun.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 * 先搭建需要的类和接口基本结构，实体类category，数据访问层Mapper接口，业务层接口categoryservice，业务层实现类，控制层
 *
 *
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * 因为分类数据库唯一，若重复会被全局异常处理器捕捉
     * 执行过程：1，页面发送ajax请求，将新增分类窗口输入的数据以json形式提交到服务端 2，服务端Controller接收页面提交的数据并调用service将数据进行保存
     * 3，service调用mapper操作数据库，保存数据
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
      log.info("category:{}",category);
      categoryService.save(category);
      return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     * 1，执行过程页面发送ajax请求，将分页查询参数（page，pageSize）提交到服务端 2，服务端Controller接收页面提交的数据并调用service查询数据
     * 3，service调用mapper操作数据库，查询分页数据 4，Controller将查询到的分页数据响应给页面
     * 5，也面接收到分页数据并通过ElementUI的Table展示到页面上
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件,根据sort进行排序，调用qW的oderby，ASC升序
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);


    }



    /**
     * 根据id删除分类
     *   当前分类关联菜品或者套餐时此分类不允许删除，所有先判断有无关联
     *   这里要返回string是因为前端判断删除成功是根据code为1，Result有code,T用最简单的string就行，success会使code为1
     *     前端传的是ids
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类 id为{}",ids);

        //这里是调用IService的remove方法但不能满足先判断条件，即自己写remove
        //categoryService.removeById(ids);

        categoryService.remove(ids);
        return Result.success("分类信息删除成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);

        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }


    /**
     * 根据条件查询分类数据
     * 因为前端的药品管理中新增药品的下拉框会先申请分类，所有在这个地方写
     * list()里有两种写法 一、直接接收type，二声明category实体这样type也会封装到实体类（这种更好通用性更强。后期可能还会用到其他参数）
     * 请求路径以及请求发送可以在food.js里面看
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件，根据传过来的type进行等值查询
        //,一、这里最好先判断保证type不为空这种情况下再添加条件，二、具体条件用：：形式 三、具体值用传过来的。这样就完成动态添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件，category是有sort顺序的,先根据；；getsort升学排序，相同情况下再用更新时间降序排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //调用service并返回list集合
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
