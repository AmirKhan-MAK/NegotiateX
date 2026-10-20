package com.negotiatex.dto.request;

import java.math.BigDecimal;

public class CounterOfferRequest {
    private BigDecimal price;
    private String message;

    public CounterOfferRequest() {}

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
