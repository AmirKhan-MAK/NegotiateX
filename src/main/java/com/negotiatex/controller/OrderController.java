package com.negotiatex.controller;

import com.negotiatex.entity.OrderStatus;
import com.negotiatex.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body, Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(401).body("Unauthorized");
            OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
            return ResponseEntity.ok(orderService.updateOrderStatus(id, newStatus, principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
