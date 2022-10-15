package com.example.springboot_shixun.filter;

import com.example.springboot_shixun.common.BaseContext;
import com.example.springboot_shixun.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import com.alibaba.fastjson.JSON;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 分页查询也会调用loginCheckFilter，这是另一个线程了
 */
//过滤器注释，名称和过滤路径
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class loginCheckFilter implements Filter {
    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //转型，httpservicerequest继承了Service Request也就是说这个转型是从最顶级的父类移到下一级父类
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();
        //{}是占位符后面，加东西就行与“”+“”一样，，URI只是多一个服务器路径，返回部分路径
        log.info("拦截到的请求：{}",requestURI);

        //定义不需要处理的请求的路径，不给看动态数据，静态资源就可以（可优化）
        //若请求为/backend/index.html则需通配符匹配
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**",
          "/common/**",
          "/user/sendMsg",//移动端发送消息
          "/user/login"//移动端登陆

        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls,requestURI);

        //3，如果不需要处理,则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1,判断登陆状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

          Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2,判断移动端登陆状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //5,如果未登录则返回未登录结果，通过数据流方式向客户端页面相应数据
        //因为writ()方法是需要一个字符串，所以需要将java对象转为json字符串响应给客户端
        //在页面中都有request.js，里面有写返回NOTLOGIN则跳转为登陆界面
        //登陆写后，必须写登出功能，否则session一直没请，页面直接打不会跳转回登陆界面
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        //根据前端需求写后端代码，可以直接返回，但前端代码有要求，后端按要求写
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
