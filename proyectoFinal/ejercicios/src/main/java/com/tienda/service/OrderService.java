package com.tienda.service;

import com.tienda.dto.OrderRequest;
import com.tienda.dto.OrderResponse;
import com.tienda.exception.BusinessException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.*;
import com.tienda.repository.CustomerRepository;
import com.tienda.repository.OrderRepository;
import com.tienda.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * EJERCICIO 5 - Capa de servicio para ordenes.
 *
 * Logica critica implementada aqui:
 * 1. Verificar que el cliente exista.
 * 2. Para cada item: verificar que el producto exista y tenga stock suficiente.
 * 3. Reducir el stock de cada producto (operacion transaccional).
 * 4. Calcular el total sumando subtotales de cada item.
 * 5. Persistir la orden con sus items en una sola transaccion atomica.
 *
 * @Transactional garantiza que si algo falla a mitad del proceso
 * (por ejemplo, stock insuficiente en el segundo item), ninguna operacion
 * se persiste en la base de datos (rollback automatico).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> findByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente", customerId);
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    /**
     * Crea una nueva orden aplicando la logica de negocio completa.
     * Todo ocurre dentro de una sola transaccion para garantizar atomicidad.
     */
    @Transactional
    public OrderResponse create(OrderRequest request) {
        // 1. Verificar que el cliente existe
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.customerId()));

        // 2. Construir los items validando stock
        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderRequest.OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", itemReq.productId()));

            // Validar stock suficiente
            if (product.getStock() < itemReq.quantity()) {
                throw new BusinessException(
                        "Stock insuficiente para el producto '" + product.getName() +
                        "'. Disponible: " + product.getStock() +
                        ", solicitado: " + itemReq.quantity()
                );
            }

            // Reducir stock
            product.setStock(product.getStock() - itemReq.quantity());
            productRepository.save(product);

            // Construir item con precio historico al momento de la compra
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            items.add(item);
            total += item.getSubtotal();
        }

        // 3. Crear y persistir la orden
        Order order = Order.builder()
                .customer(customer)
                .total(total)
                .status(OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        // 4. Asociar items a la orden ya persistida
        final Order savedOrder = order;
        items.forEach(item -> item.setOrder(savedOrder));
        savedOrder.getItems().addAll(items);

        return OrderResponse.fromEntity(orderRepository.save(savedOrder));
    }
}

