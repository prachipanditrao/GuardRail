package com.example.demo.repository;

import com.example.demo.domain.Order;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Repository
public class OrderRepository {
    private final ConcurrentHashMap<UUID, Order> db = new ConcurrentHashMap<>();

    public Order save(Order order) {
        db.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(db.get(id));
    }
}