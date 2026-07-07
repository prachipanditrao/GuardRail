package com.example.demo.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final List<OrderItem> items;
    private String status;
    private final OffsetDateTime createdAt;

    public Order(UUID id, UUID customerId, List<OrderItem> items, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}