package com.negotiatex.repository;

import com.negotiatex.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, String> {
    SellerProfile findByUserId(String userId);
}
