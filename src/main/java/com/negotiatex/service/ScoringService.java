package com.negotiatex.service;

import com.negotiatex.repository.UserBehaviorEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    @Autowired
    private UserBehaviorEventRepository behaviorRepo;

    /**
     * Calculates a buyer trust score between 0.0 and 1.0
     * @param userId buyer id
     * @return discount factor, e.g. 0.90 means 10% discount eligible
     */
    @Cacheable(value = "buyerScores", key = "#userId")
    public double calculateBuyerScore(String userId) {
        long dealClosedCount = behaviorRepo.countByUserIdAndEventType(userId, "DEAL_CLOSED");
        long offerMadeCount = behaviorRepo.countByUserIdAndEventType(userId, "OFFER_MADE");
        long viewsCount = behaviorRepo.countByUserIdAndEventType(userId, "PRODUCT_VIEW");

        double score = 0.0;

        // Rule 1: High engagement gives slight bump
        if (viewsCount > 10) score += 0.05;

        // Rule 2: Actually participating in negotiations shows intent
        if (offerMadeCount > 0) {
            score += Math.min(offerMadeCount * 0.02, 0.10);
        }

        // Rule 3: Reliability. If they close deals, they are highly trusted.
        if (dealClosedCount > 0) {
            score += Math.min(dealClosedCount * 0.10, 0.40);
        }

        // Cap score at 0.55 (represents up to max 55% discount off the markup)
        // Wait, the API will use this to drop the price closer to floor price.
        // A score of 1.0 means they get the absolute floor price.
        return Math.min(score, 1.0);
    }
}
