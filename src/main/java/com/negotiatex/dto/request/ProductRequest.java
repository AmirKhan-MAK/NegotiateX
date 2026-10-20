package com.negotiatex.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductRequest {
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal floorPrice;
    private Integer stock;
    private Boolean negotiable;
    private Boolean isNearExpiry;
    private LocalDate expiryDate;
    private String imageUrl;
    private Boolean aiNegotiationEnabled;
    private BigDecimal aiTargetPrice;
    private List<BulkTierRequest> bulkTiers;

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getFloorPrice() { return floorPrice; }
    public void setFloorPrice(BigDecimal floorPrice) { this.floorPrice = floorPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getNegotiable() { return negotiable; }
    public void setNegotiable(Boolean negotiable) { this.negotiable = negotiable; }
    public Boolean getIsNearExpiry() { return isNearExpiry; }
    public void setIsNearExpiry(Boolean isNearExpiry) { this.isNearExpiry = isNearExpiry; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<BulkTierRequest> getBulkTiers() { return bulkTiers; }
    public void setBulkTiers(List<BulkTierRequest> bulkTiers) { this.bulkTiers = bulkTiers; }
    public Boolean getAiNegotiationEnabled() { return aiNegotiationEnabled; }
    public void setAiNegotiationEnabled(Boolean aiNegotiationEnabled) { this.aiNegotiationEnabled = aiNegotiationEnabled; }
    public BigDecimal getAiTargetPrice() { return aiTargetPrice; }
    public void setAiTargetPrice(BigDecimal aiTargetPrice) { this.aiTargetPrice = aiTargetPrice; }
}
