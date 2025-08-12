package com.example.gbmzcpb.jpa;

import com.example.gbmzcpb.entity.SatisfactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SatisfactionCategoryRepository extends JpaRepository<SatisfactionCategory, Integer> {
    SatisfactionCategory findByCode(String code);
    List<SatisfactionCategory> findBynameContaining(String name);

}
