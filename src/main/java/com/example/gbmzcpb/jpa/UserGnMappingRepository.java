package com.example.gbmzcpb.jpa;

import com.example.gbmzcpb.entity.UserGnMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGnMappingRepository extends JpaRepository<UserGnMapping, Integer> {

    /**
     * 根据用户角色查找对应的功能名称
     * @param occupation 用户角色
     * @return 功能名称列表
     */
    List<UserGnMapping> findByOccupation(String occupation);
}
