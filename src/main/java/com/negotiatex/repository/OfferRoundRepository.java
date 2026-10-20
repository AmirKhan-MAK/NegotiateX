package com.negotiatex.repository;

import com.negotiatex.entity.OfferRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRoundRepository extends JpaRepository<OfferRound, String> {
    List<OfferRound> findByOfferIdOrderByRoundNoAsc(String offerId);
}
