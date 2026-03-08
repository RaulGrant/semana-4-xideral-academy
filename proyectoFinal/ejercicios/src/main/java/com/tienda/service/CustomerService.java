package com.tienda.service;

import com.tienda.dto.CustomerRequest;
import com.tienda.dto.CustomerResponse;
import com.tienda.exception.BusinessException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Customer;
import com.tienda.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EJERCICIO 5 - Capa de servicio para clientes.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository repository;

    public List<CustomerResponse> findAll() {
        return repository.findAll().stream()
                .map(CustomerResponse::fromEntity)
                .toList();
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.fromEntity(
                repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente", id))
        );
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un cliente con el email: " + request.email());
        }
        Customer customer = Customer.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .build();
        return CustomerResponse.fromEntity(repository.save(customer));
    }
}

