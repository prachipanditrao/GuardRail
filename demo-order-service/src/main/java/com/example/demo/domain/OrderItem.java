package com.example.demo.domain;

import java.util.UUID;

public class OrderItem {
    private final UUID productId;
    private final Integer quantity;

    public OrderItem(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
}