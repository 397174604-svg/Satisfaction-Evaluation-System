package com.example.gbmzcpb.jpa;

import com.example.gbmzcpb.entity.SatisfactionItem;
import com.example.gbmzcpb.entity.SatisfactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SatisfactionItemRepository extends JpaRepository<SatisfactionItem, Integer> {
    List<SatisfactionItem> findByCategory(SatisfactionCategory category);
    List<SatisfactionItem> findByCategoryCode(String categoryCode);
    List<SatisfactionItem> findByContentContaining(String content);
}
