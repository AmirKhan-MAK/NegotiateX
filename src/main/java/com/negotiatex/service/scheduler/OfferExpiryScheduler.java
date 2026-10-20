package com.negotiatex.service.scheduler;

import com.negotiatex.entity.Offer;
import com.negotiatex.entity.OfferStatus;
import com.negotiatex.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class OfferExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(OfferExpiryScheduler.class);

    @Autowired
    private OfferRepository offerRepository;

    // Run every minute
    @Scheduled(fixedRate = 60000)
    public void executeExpiryCheck() {
        log.info("Running Scheduled Task: Checking for expired negotiations...");

        List<OfferStatus> activeStatuses = Arrays.asList(OfferStatus.PENDING, OfferStatus.COUNTERED);
        List<Offer> expiredOffers = offerRepository.findByStatusInAndExpiresAtBefore(activeStatuses, LocalDateTime.now());

        if (!expiredOffers.isEmpty()) {
            for (Offer offer : expiredOffers) {
                log.info("Expiring Offer ID: {}", offer.getId());
                offer.setStatus(OfferStatus.EXPIRED);
            }
            // Batch save
            offerRepository.saveAll(expiredOffers);
            log.info("Successfully expired {} old negotiations.", expiredOffers.size());
        }
    }
}
