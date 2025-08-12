package com.example.gbmzcpb.service;

import com.example.gbmzcpb.entity.UserGnMapping;
import com.example.gbmzcpb.jpa.UserGnMappingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGnMappingService {

    @Autowired
    private UserGnMappingRepository userGnMappingRepository;

    /**
     * 初始化用户角色与功能的映射关系
     */
    @PostConstruct
    public void initMappings() {
        // 如果数据库中已有数据，则不再初始化
        if (userGnMappingRepository.count() > 0) {
            return;
        }

        // 管理员角色的功能映射
        UserGnMapping adminMapping1 = new UserGnMapping();
        adminMapping1.setOccupation("管理员");
        adminMapping1.setGnName("预览收集信息");

        UserGnMapping adminMapping2 = new UserGnMapping();
        adminMapping2.setOccupation("管理员");
        adminMapping2.setGnName("管理收集表");

        // 用户角色的功能映射
        UserGnMapping userMapping1 = new UserGnMapping();
        userMapping1.setOccupation("用户");
        userMapping1.setGnName("编写评测表");

        UserGnMapping userMapping2 = new UserGnMapping();
        userMapping2.setOccupation("用户");
        userMapping2.setGnName("预览收集信息");

        // 保存所有映射关系
        List<UserGnMapping> mappings = new ArrayList<>();
        mappings.add(adminMapping1);
        mappings.add(adminMapping2);
        mappings.add(userMapping1);
        mappings.add(userMapping2);
        userGnMappingRepository.saveAll(mappings);
    }

    /**
     * 根据用户角色获取功能名称列表
     * @param occupation 用户角色
     * @return 功能名称列表
     */
    public List<String> getGnNamesByOccupation(String occupation) {
        return userGnMappingRepository.findByOccupation(occupation)
                .stream()
                .map(UserGnMapping::getGnName)
                .collect(Collectors.toList());
    }
}
