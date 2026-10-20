package com.negotiatex.service;

import com.negotiatex.entity.Order;
import com.negotiatex.entity.OrderStatus;
import com.negotiatex.entity.Payment;
import com.negotiatex.repository.OrderRepository;
import com.negotiatex.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MockPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Simulates initializing an order with a Payment Gateway (like Razorpay).
     */
    @Transactional
    public Payment createPaymentTransaction(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != com.negotiatex.entity.OrderStatus.PLACED) {
            throw new RuntimeException("Order cannot be paid for in this status.");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice());
        payment.setCurrency("USD");
        payment.setTransactionId("mock_txn_" + UUID.randomUUID().toString().substring(0, 8));
        payment.setStatus("PENDING");

        return paymentRepository.save(payment);
    }

    /**
     * Simulates a successful backend webhook or signature verification from Razorpay.
     */
    @Transactional
    public Payment verifyPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!payment.getStatus().equals("PENDING")) {
            throw new RuntimeException("Payment is already processed.");
        }

        payment.setStatus("PAID");
        payment = paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.DELIVERED); // Simplification for MVP: Paid implies fulfillment 
        orderRepository.save(order);

        return payment;
    }
}
