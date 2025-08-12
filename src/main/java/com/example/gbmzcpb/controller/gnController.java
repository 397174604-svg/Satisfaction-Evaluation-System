package com.example.gbmzcpb.controller;

import com.example.gbmzcpb.entity.gn;
import com.example.gbmzcpb.jpa.gnRepository;
import com.example.gbmzcpb.service.UserGnMappingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice //全局数据绑定
public class gnController {
    @Autowired
    private gnRepository gnRepository1;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserGnMappingService userGnMappingService;

    @ModelAttribute("gnItems")
    public List<gn> gnItems() {
        String occ = (String) request.getSession().getAttribute("occupation");
        List<gn> allItems = gnRepository1.findAll();

        List<String> allowedGnNames = userGnMappingService.getGnNamesByOccupation(occ);
        return allItems.stream()
                .filter(gn -> allowedGnNames.stream().anyMatch(allowedName -> gn.getGn().contains(allowedName)))
                .collect(Collectors.toList());
    }

}
