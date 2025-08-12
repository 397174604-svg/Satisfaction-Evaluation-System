package com.example.gbmzcpb.controller;

import com.example.gbmzcpb.entity.User;
import com.example.gbmzcpb.jpa.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginControler {

    // 处理根路径请求
    @GetMapping("/")
    public String index() {
        return "redirect:login";
    }
    @GetMapping("error")
    public String error(){
        return "error";
    }

  @GetMapping("common")
  public String common() {
    return "common";
  }

    // 显示登录页面
    @GetMapping("login")
    public String showLoginPage() {
        return "login";
    }

    @Autowired
    UserRepository userRepository;

    //注销用户登录 HttpServletRequest表当前的网页请求
    @GetMapping("logout")
    public String login(HttpServletRequest request) {
        //从当前用户请求中删除当前用户信息
        request.getSession().removeAttribute("user");
        return "redirect:login";
    }

    //验证登录合法性
    @PostMapping("valid")
    public String valid(HttpServletRequest request) {
        //获取当前用户请求中的用户名和密码
        String username = request.getParameter("user");
        String password = request.getParameter("pass");
        User userInfo = userRepository.findByUsernameAndPassword(username, password);
        //从数据库中查询当前用户名和密码是否合法
        if (userRepository.existsByUsernameAndPassword(username, password)) {
            //合法则将当前用户信息保存到当前用户请求中
            request.getSession().setAttribute("user", username);
            request.getSession().setAttribute("occupation", userInfo.getOccupation());
            return "redirect:common";
        } else {
            //不合法则返回登录页面
            return "redirect:error";
        }
    }

    // 显示注册页面
    @GetMapping("register")
    public String showRegisterPage() {
        return "register";
    }

    // 处理注册请求
    @PostMapping("register")
    public String register(HttpServletRequest request) {
        // 获取注册表单数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String occupation = request.getParameter("occupation");

        User user;
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            // 如果用户存在，更新用户信息
            user = userRepository.findByUsername(username);
            user.setPassword(password);
            user.setOccupation(occupation);
        } else {
            // 创建新用户
            user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setOccupation(occupation);
        }

        // 保存用户到数据库
        userRepository.save(user);

//        // 将用户信息保存到session中（自动登录）
//        request.getSession().setAttribute("user", username);
//        request.getSession().setAttribute("occupation", occupation);

        // 注册/更新成功后直接重定向到login页面
        return "redirect:login";
    }
}
