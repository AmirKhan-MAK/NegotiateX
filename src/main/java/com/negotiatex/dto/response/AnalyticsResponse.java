package com.negotiatex.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AnalyticsResponse {
    private BigDecimal totalGmv;
    private long totalOrders;
    private long totalOffersReceived;
    private double offerConversionRate; // (Orders via offers) / (Total Offers)
    private List<Map<String, Object>> topSellingProducts;
	public BigDecimal getTotalGmv() {
		return totalGmv;
	}
	public void setTotalGmv(BigDecimal totalGmv) {
		this.totalGmv = totalGmv;
	}
	public long getTotalOrders() {
		return totalOrders;
	}
	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}
	public long getTotalOffersReceived() {
		return totalOffersReceived;
	}
	public void setTotalOffersReceived(long totalOffersReceived) {
		this.totalOffersReceived = totalOffersReceived;
	}
	public double getOfferConversionRate() {
		return offerConversionRate;
	}
	public void setOfferConversionRate(double offerConversionRate) {
		this.offerConversionRate = offerConversionRate;
	}
	public List<Map<String, Object>> getTopSellingProducts() {
		return topSellingProducts;
	}
	public void setTopSellingProducts(List<Map<String, Object>> topSellingProducts) {
		this.topSellingProducts = topSellingProducts;
	}
    
    
}
