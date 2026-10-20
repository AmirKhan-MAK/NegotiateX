package com.negotiatex.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OfferRoundResponse {
    private String id;
    private Integer roundNo;
    private String actorName;
    private String actorRole;
    private BigDecimal price;
    private String action;
    private String message;
    private LocalDateTime createdAt;

    public OfferRoundResponse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getRoundNo() { return roundNo; }
    public void setRoundNo(Integer roundNo) { this.roundNo = roundNo; }
    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
