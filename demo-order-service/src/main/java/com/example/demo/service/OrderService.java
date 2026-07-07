package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(UUID customerId, List<OrderItem> items) {
        Order order = new Order(UUID.randomUUID(), customerId, items, "CREATED", OffsetDateTime.now());
        return orderRepository.save(order);
    }

    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    public Order updateStatus(UUID id, String status) {
        Order order = getOrder(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}