package com.example.gbmzcpb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class gn {
    @Id
    private String gn;  // 功能名称
    private String lj;  // 链接
}
