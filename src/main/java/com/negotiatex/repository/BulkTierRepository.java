package com.negotiatex.repository;

import com.negotiatex.entity.BulkTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BulkTierRepository extends JpaRepository<BulkTier, String> {
    List<BulkTier> findByProductId(String productId);
}
