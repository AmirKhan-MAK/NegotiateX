package com.negotiatex.repository;

import com.negotiatex.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.negotiatex.entity.OfferStatus;

@Repository
public interface OfferRepository extends JpaRepository<Offer, String> {
    List<Offer> findByBuyerId(String buyerId);
    List<Offer> findBySellerId(String sellerId);
    List<Offer> findByProductId(String productId);
    List<Offer> findByStatusInAndExpiresAtBefore(List<OfferStatus> statuses, LocalDateTime time);
}
