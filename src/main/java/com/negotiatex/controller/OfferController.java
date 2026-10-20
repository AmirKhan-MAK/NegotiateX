package com.negotiatex.controller;

import com.negotiatex.dto.request.CounterOfferRequest;
import com.negotiatex.dto.request.CreateOfferRequest;
import com.negotiatex.dto.response.OfferResponse;
import com.negotiatex.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferResponse> createOffer(@RequestBody CreateOfferRequest request, Principal principal) {
        return ResponseEntity.ok(offerService.createOffer(request, principal.getName()));
    }

    @PostMapping("/{id}/counter")
    public ResponseEntity<OfferResponse> counterOffer(@PathVariable String id, @RequestBody CounterOfferRequest request, Principal principal) {
        return ResponseEntity.ok(offerService.counterOffer(id, request, principal.getName()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<OfferResponse> acceptOffer(@PathVariable String id, Principal principal) {
        return ResponseEntity.ok(offerService.acceptOffer(id, principal.getName()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<OfferResponse> rejectOffer(@PathVariable String id, Principal principal) {
        return ResponseEntity.ok(offerService.rejectOffer(id, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<OfferResponse>> getMyOffers(Principal principal) {
        return ResponseEntity.ok(offerService.getMyOffers(principal.getName()));
    }
}
