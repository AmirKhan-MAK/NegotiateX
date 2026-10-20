package com.negotiatex.dto.request;

import java.math.BigDecimal;

public class BulkTierRequest {
    private Integer minQty;
    private Integer maxQty;
    private BigDecimal price;

    public Integer getMinQty() { return minQty; }
    public void setMinQty(Integer minQty) { this.minQty = minQty; }
    public Integer getMaxQty() { return maxQty; }
    public void setMaxQty(Integer maxQty) { this.maxQty = maxQty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
