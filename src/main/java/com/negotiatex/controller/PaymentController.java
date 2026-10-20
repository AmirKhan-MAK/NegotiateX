package com.negotiatex.controller;

import com.negotiatex.entity.Payment;
import com.negotiatex.service.MockPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private MockPaymentService paymentService;

    @PostMapping("/init")
    public ResponseEntity<Map<String, String>> initializePayment(@RequestBody Map<String, String> request) {
        String orderId = request.get("orderId");
        if (orderId == null) {
            return ResponseEntity.badRequest().build();
        }

        Payment payment = paymentService.createPaymentTransaction(orderId);

        Map<String, String> response = new HashMap<>();
        response.put("transactionId", payment.getTransactionId());
        response.put("status", payment.getStatus());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, String> request) {
        String transactionId = request.get("transactionId");
        if (transactionId == null) {
            return ResponseEntity.badRequest().build();
        }

        Payment payment = paymentService.verifyPayment(transactionId);

        Map<String, String> response = new HashMap<>();
        response.put("status", payment.getStatus());
        response.put("orderStatus", payment.getOrder().getStatus().name());

        return ResponseEntity.ok(response);
    }
}
