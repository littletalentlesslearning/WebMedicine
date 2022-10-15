package com.example.springboot_shixun.common;


import lombok.Data;
import java.util.HashMap;
import java.util.Map;
/**
 * 返回结果类，是一个通用结果类，服务器相应的所有结果最终都会包装成此种类型返回给前端
 * 封装为对象的时候，这个对象可以封装具体返回信息+操作是否正确的信息，假设不封装对象，操作是否正确不知道
 * 后期如果发生错误可以快速找出是返回数据错误还是操作错误
 */
@Data
public class Result<T> {
    private Integer code;//编码：1成功，0和其它数字为失败

    private String msg;//错误信息

    //java对数据要求严格不同语言对数据的要求不一样，json格式就是一个规范
    //数据可以是Object但要进行强制数据转换，效率低
    private T data;//数据，把实体存入，在前端的login。html可以转成json数据

    private Map map = new HashMap();//动态数据

    //静态方法需之命是泛型，<T>表示该方法是泛型方法，R<T>就是返回值
    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Result<T> add(String key,Object value) {
        this.map.put(key,value);
        return this;
    }

}
