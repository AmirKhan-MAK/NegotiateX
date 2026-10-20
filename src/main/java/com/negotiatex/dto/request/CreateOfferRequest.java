package com.negotiatex.dto.request;

import java.math.BigDecimal;

public class CreateOfferRequest {
    private String productId;
    private BigDecimal price;
    private Integer quantity;
    private String message;

    public CreateOfferRequest() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
