package com.example.gbmzcpb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.gbmzcpb.entity.SatisfactionItem;

@Data //get，set方法
@Entity //建表的注解
public class Person {
    @Id //sql主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主件生成策略 自增
    private Integer id;
    private String name;
    private int age;
    private String sex;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "person_satisfaction_items",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "satisfaction_item_id")
    )
    private List<SatisfactionItem> satisfactionItems;

    public List<SatisfactionItem> getSatisfactionItems() {
        return satisfactionItems != null ? satisfactionItems : new ArrayList<>();
    }

    public List<SatisfactionItem> getQzItems() {
        return getSatisfactionItems().stream()
            .filter(item -> "QZ".equals(item.getCategory().getCode()))
            .collect(Collectors.toList());
    }

    public List<SatisfactionItem> getXzItems() {
        return getSatisfactionItems().stream()
            .filter(item -> "XZ".equals(item.getCategory().getCode()))
            .collect(Collectors.toList());
    }
}
