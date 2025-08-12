package com.example.gbmzcpb.controller;

import com.example.gbmzcpb.entity.Person;
import com.example.gbmzcpb.entity.SatisfactionCategory;
import com.example.gbmzcpb.entity.SatisfactionItem;
import com.example.gbmzcpb.entity.User;
import com.example.gbmzcpb.jpa.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class qxController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/browse")
    public String browse(HttpServletRequest request, Model model){
        // 获取所有用户
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "browse";
    }
}
