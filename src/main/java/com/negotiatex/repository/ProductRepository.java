package com.negotiatex.repository;

import com.negotiatex.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findBySellerId(String sellerId);
    List<Product> findByCategoryId(String categoryId);
}
