package com.example.demo.controller;

import com.example.demo.api.OrdersApi;
import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.UpdateStatusRequest;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class OrderController implements OrdersApi {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        List<OrderItem> domainItems = createOrderRequest.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        Order domainOrder = orderService.createOrder(createOrderRequest.getCustomerId(), domainItems);
        return new ResponseEntity<>(mapToResponse(domainOrder), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(UUID id) {
        Order domainOrder = orderService.getOrder(id);
        return ResponseEntity.ok(mapToResponse(domainOrder));
    }

    @Override
    public ResponseEntity<OrderResponse> updateOrderStatus(UUID id, UpdateStatusRequest updateStatusRequest) {
        String statusStr = updateStatusRequest.getStatus().getValue();
        Order updatedOrder = orderService.updateStatus(id, statusStr);
        return ResponseEntity.ok(mapToResponse(updatedOrder));
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            List<com.example.demo.dto.OrderItem> dtoItems = order.getItems().stream()
                    .map(domainItem -> {
                        com.example.demo.dto.OrderItem dtoItem = new com.example.demo.dto.OrderItem();
                        dtoItem.setProductId(domainItem.getProductId());
                        dtoItem.setQuantity(domainItem.getQuantity());
                        return dtoItem;
                    })
                    .collect(Collectors.toList());
            response.setItems(dtoItems);
        }

        return response;
    }
}