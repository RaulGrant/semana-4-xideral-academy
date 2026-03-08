package com.tienda.controller;

import com.tienda.dto.CustomerRequest;
import com.tienda.dto.CustomerResponse;
import com.tienda.dto.OrderResponse;
import com.tienda.service.CustomerService;
import com.tienda.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EJERCICIO 5 - Controlador REST para clientes.
 * Incluye endpoint para consultar las ordenes de un cliente especifico.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    /**
     * GET /api/v1/customers/{id}/orders
     * Endpoint anidado: representa la relacion "ordenes del cliente X".
     * Esta convencion de rutas anidadas es tipica en APIs REST bien disenadas.
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findByCustomerId(id));
    }
}

