package com.tienda.dto;

import com.tienda.model.Order;
import com.tienda.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EJERCICIO 5 - DTO de salida para ordenes.
 * Expone el detalle completo incluyendo los items del pedido.
 */
public record OrderResponse(
        Long id,
        Long customerId,
        String customerName,
        List<OrderItemResponse> items,
        Double total,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public record OrderItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            Double unitPrice,
            Double subtotal
    ) {}

    public static OrderResponse fromEntity(Order o) {
        List<OrderItemResponse> itemDtos = o.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                )).toList();

        return new OrderResponse(
                o.getId(),
                o.getCustomer().getId(),
                o.getCustomer().getName(),
                itemDtos,
                o.getTotal(),
                o.getStatus(),
                o.getCreatedAt()
        );
    }
}

