package com.negotiatex.service;

import com.negotiatex.entity.Offer;
import com.negotiatex.entity.Order;
import com.negotiatex.entity.OrderStatus;
import com.negotiatex.entity.Product;
import com.negotiatex.event.OfferAcceptedEvent;
import com.negotiatex.repository.OfferRepository;
import com.negotiatex.repository.OrderRepository;
import com.negotiatex.repository.ProductRepository;
import com.negotiatex.repository.UserRepository;
import com.negotiatex.dto.response.OrderResponse;
import com.negotiatex.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @EventListener
    @Transactional
    public void handleOfferAcceptedEvent(OfferAcceptedEvent event) {
        Offer offer = offerRepository.findById(event.getOfferId()).orElseThrow(() -> new RuntimeException("Offer not found: " + event.getOfferId()));
        Product product = offer.getProduct();

        Order order = new Order();
        order.setBuyer(offer.getBuyer());
        order.setSeller(offer.getSeller());
        order.setOffer(offer);
        order.setProduct(product);
        order.setQuantity(offer.getQuantity());
        order.setUnitPrice(offer.getFinalPrice());
        order.setTotalPrice(offer.getFinalPrice().multiply(new BigDecimal(offer.getQuantity())));
        order.setStatus(OrderStatus.PLACED);

        orderRepository.save(order);

        // Deduct Inventory Stock
        int stockRemaining = product.getStock() - offer.getQuantity();
        if (stockRemaining < 0) stockRemaining = 0;
        product.setStock(stockRemaining);
        productRepository.save(product);
        
        System.out.println("=== EVENT LISTENER TRIGGERED ===");
        System.out.println("✅ Order generated automatically from accepted offer: " + offer.getId());
    }

    public List<OrderResponse> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        List<Order> orders;
        if (user.getRole() == com.negotiatex.entity.Role.SELLER) {
            orders = orderRepository.findBySellerId(user.getId());
        } else {
            orders = orderRepository.findByBuyerId(user.getId());
        }
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus, String userEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if (!order.getSeller().getId().equals(user.getId())) {
             throw new RuntimeException("Only sellers can update status");
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse resp = new OrderResponse();
        resp.setId(order.getId());
        resp.setProductName(order.getProduct().getName());
        resp.setBuyerName(order.getBuyer().getName());
        resp.setSellerName(order.getSeller().getName());
        resp.setQuantity(order.getQuantity());
        resp.setUnitPrice(order.getUnitPrice());
        resp.setTotalPrice(order.getTotalPrice());
        resp.setStatus(order.getStatus().name());
        resp.setCreatedAt(order.getCreatedAt());
        return resp;
    }
}
