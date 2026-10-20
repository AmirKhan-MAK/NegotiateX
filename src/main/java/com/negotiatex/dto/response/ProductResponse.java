package com.negotiatex.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductResponse {
	private String id;
	private String sellerName;
	private String categoryName;
	private String name;
	private String description;
	private BigDecimal basePrice;
	private BigDecimal floorPrice;
	private Integer stock;
	private Boolean negotiable;
	private Boolean isNearExpiry;
	private LocalDate expiryDate;
	private String imageUrl;
	private LocalDateTime createdAt;
	
	// Gamification / Personalization
	private BigDecimal personalizedDiscountPrice;
	private Boolean isBestDealUnlocked;

	// Explicit Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getFloorPrice() {
		return floorPrice;
	}

	public void setFloorPrice(BigDecimal floorPrice) {
		this.floorPrice = floorPrice;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Boolean getNegotiable() {
		return negotiable;
	}

	public void setNegotiable(Boolean negotiable) {
		this.negotiable = negotiable;
	}

	public Boolean getIsNearExpiry() {
		return isNearExpiry;
	}

	public void setIsNearExpiry(Boolean nearExpiry) {
		isNearExpiry = nearExpiry;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public BigDecimal getPersonalizedDiscountPrice() {
		return personalizedDiscountPrice;
	}

	public void setPersonalizedDiscountPrice(BigDecimal personalizedDiscountPrice) {
		this.personalizedDiscountPrice = personalizedDiscountPrice;
	}

	public Boolean getIsBestDealUnlocked() {
		return isBestDealUnlocked;
	}

	public void setIsBestDealUnlocked(Boolean bestDealUnlocked) {
		isBestDealUnlocked = bestDealUnlocked;
	}
}
