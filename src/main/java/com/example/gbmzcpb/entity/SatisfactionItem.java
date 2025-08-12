package com.example.gbmzcpb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SatisfactionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;    // 选项内容

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SatisfactionCategory category;
}
