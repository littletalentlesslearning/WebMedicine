package com.example.springboot_shixun.controller;

import com.example.springboot_shixun.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 * 这里新增的需要在过滤器添加路径，否则单纯打回，没实现下面功能，前端没写跳转的功能页面不变但没实现功能
 * 去过滤器加common/**
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //读取yml文件指定内容的方法之一
    @Value("${medicine.path}")
    private String basePath;

    /**
     * 文件上传
     * 文件上传也称upload，前端使用ElementUI提供的upload上传组件
     * 服务端接收客户端页面上传的文件，通常都会使用Apache的两个组件：commons_fileupload与commons-io，spring框架在spring-web对文件上传进行封装
     * 只需在Controller方法中声明MultipartFile类型的参数即可接收上传文件（固定），
     * file不能随便写名字，必须与浏览器中显示的from-data下的content-Disposition中name保存一致
     * 这里可以使用@RequestPart("file"),这样形参可以随意取名字
     *
     * 一先写出保存到固定路径加文件名，二使路径动态，三使文件名动态，四防止yml路径无文件夹需判断并创建
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //获得原始文件名file.getOriginalFilename()，但会有重名危险后面上传的会覆盖
        String originalFilename = file.getOriginalFilename();//abc.jpg
        //接收后缀名从.开始截取，
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
       /* //如果使用split需要注意用（“\\.”）.注意： . $ | * 等转义字符，必须得加 \\。,split[]是从0开始
       String[] split = originalFilename.split("\\.");
        String suffix1 = "." + split[1];*/


        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖,随机生成字符串，但还差后缀
        String fileName = UUID.randomUUID().toString() + suffix;//das.jpg


        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //目录不存在，需要创建
            dir.mkdir();
        }

        try {
            //将临时文件转存到指定位置,把位置写成动态的，别固定像D:\\hello.jpg,yml路径可以是D:\a\,然后改为basePath+"hello.jpg"
            //将"hello.jpg"改为动态的
            //测试时记得删除之前存的位置中的文件否则报错
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //这个文件上传功能是为后面的药品添加服务，即需要返回FileName，
        // 页面需要这个文件名，后面需要完成新增药品，新增药品需要先传文件图片，文件名需保存到数据库里药品表里，然后提交表单数据
        return Result.success(fileName);
    }

    /**
     * 文件下载
     * 不需要返回值通过输出流向浏览器页面来显回数据,前端是有发送name的需要接收，而输出流需要通过respond获得
     * @param name
     * @param response
     */
    //发送的请求就可以请求到当前方法
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容,指定路径容易找到文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //设置想要回去的是什么类型的文件,image/jpeg代表图片文件
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            //将输入流的东西赋值给byte数组，一直读到-1就是没有
            while ((len = fileInputStream.read(bytes)) != -1) {
                //从第一个0开始写到len这么长
                outputStream.write(bytes,0,len);
                //好了后刷新
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
