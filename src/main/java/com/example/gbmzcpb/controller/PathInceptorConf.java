package com.example.gbmzcpb.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component //助视器类，启动自动加载
public class PathInceptorConf implements WebMvcConfigurer {//注册拦截器类
    //注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PathInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/login","/valid","/static/**/","/register","/error");
    }
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/login").setViewName("login");
    }


    class PathInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            //获取会话中是否有登录信息，获取与当前请求关联的 HttpSession 对象。HttpSession 是一个用来存储和管理用户会话信息的对象
            Object user = request.getSession().getAttribute("user");
            //获取客户端头信息判断来自于页面按钮还是浏览器地址的访问请求
            String Referer = request.getHeader("Referer");
            //获取当前请求的服务器域名（即主机名）
            String currentHost = request.getServerName(); // 获取当前请求的域名
            //从请求头中获取 Sec-Fetch-Mode 字段的值，表示请求的模式。
            String secFetchMode = request.getHeader("Sec-Fetch-Mode");//请求模式navigate导航，no-cors跨域请求
            //从请求头中获取 Sec-Fetch-Site 字段的值，表示请求的来源网站。
            String secFetchSite = request.getHeader("Sec-Fetch-Site");//同源same-origin,跨站cross-site
            //对当前网址信息进行判断是否是登录状态
            if (user != null) {
                //确保请求的 Referer 包含当前的域名，表明请求来自本网站。
                if (Referer != null && Referer.contains(currentHost)
                        //确保请求是由浏览器标准的导航模式发起（即点击超链接、书签等方式发起的请求）。
                        && "navigate".equalsIgnoreCase(secFetchMode)
                        //确保请求是同源请求（same-origin）或同站请求（same-site）。
                        && ("same-origin".equalsIgnoreCase(secFetchSite)
                        || "same-site".equalsIgnoreCase(secFetchSite))) {

                    return true;
                } else {
                    //当前相应对象到头信息设置重定向到登录页面
                    response.sendRedirect("/login");
                    return false;
                }
            } else {
                // forward() 方法将当前请求转发到目标资源。在此情况下，它会将请求和响应对象传递给 /login。
                request.getRequestDispatcher("/login").forward(request, response);
                return false;
            }
        }
    }
}
