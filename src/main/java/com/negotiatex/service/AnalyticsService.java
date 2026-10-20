package com.negotiatex.service;

import com.negotiatex.entity.Offer;
import com.negotiatex.entity.Order;
import com.negotiatex.entity.OrderStatus;
import com.negotiatex.repository.OfferRepository;
import com.negotiatex.repository.OrderRepository;
import com.negotiatex.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import com.negotiatex.dto.response.AnalyticsResponse;

@Service
public class AnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public AnalyticsResponse getSellerAnalytics(String sellerId) {
        AnalyticsResponse response = new AnalyticsResponse();

        List<Order> orders = orderRepository.findBySellerId(sellerId);
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalGmv(totalRevenue);

        long completedOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();
        response.setTotalOrders(completedOrders);

        List<Offer> offers = offerRepository.findBySellerId(sellerId);
        response.setTotalOffersReceived(offers.size());

        if (!offers.isEmpty()) {
            response.setOfferConversionRate((double) completedOrders / offers.size());
        } else {
            response.setOfferConversionRate(0.0);
        }

        return response;
    }
}
