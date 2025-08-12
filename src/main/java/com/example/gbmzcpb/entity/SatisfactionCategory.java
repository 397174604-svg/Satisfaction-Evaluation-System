package com.example.gbmzcpb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class SatisfactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;    // 类别名称（如"群众满意度"、"行政满意度"）
    private String code;    // 类别代码（如"QZ"、"XZ"）

    // 一对多关系cascade = CascadeType.ALL表示级联操作，即当删除父类时，子类也会被删除
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<SatisfactionItem> items;
}
