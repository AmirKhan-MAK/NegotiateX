package com.negotiatex.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@Column(length = 36)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "base_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal basePrice;

	@Column(name = "floor_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal floorPrice;

	@Column(nullable = false)
	private Integer stock = 0;

	@Column(nullable = false)
	private Boolean negotiable = true;

	@Column(name = "is_near_expiry", nullable = false)
	private Boolean isNearExpiry = false;

	@Column(name = "expiry_date")
	private LocalDate expiryDate;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "ai_negotiation_enabled", nullable = false)
	private Boolean aiNegotiationEnabled = false;

	@Column(name = "ai_target_price", precision = 12, scale = 2)
	private BigDecimal aiTargetPrice;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BulkTier> bulkTiers = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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

	public Boolean getNearExpiry() {
		return isNearExpiry;
	}

	public void setNearExpiry(Boolean nearExpiry) {
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

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<BulkTier> getBulkTiers() {
		return bulkTiers;
	}

	public void setBulkTiers(List<BulkTier> bulkTiers) {
		this.bulkTiers = bulkTiers;
	}

	public Boolean getAiNegotiationEnabled() {
		return aiNegotiationEnabled;
	}

	public void setAiNegotiationEnabled(Boolean aiNegotiationEnabled) {
		this.aiNegotiationEnabled = aiNegotiationEnabled;
	}

	public BigDecimal getAiTargetPrice() {
		return aiTargetPrice;
	}

	public void setAiTargetPrice(BigDecimal aiTargetPrice) {
		this.aiTargetPrice = aiTargetPrice;
	}
}
