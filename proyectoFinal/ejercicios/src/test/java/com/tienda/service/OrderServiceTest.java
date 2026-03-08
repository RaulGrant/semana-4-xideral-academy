package com.tienda.service;

import com.tienda.dto.OrderRequest;
import com.tienda.dto.OrderResponse;
import com.tienda.exception.BusinessException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.*;
import com.tienda.repository.CustomerRepository;
import com.tienda.repository.OrderRepository;
import com.tienda.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * EJERCICIO 5 - Tests unitarios para la logica critica de OrderService.
 * Cubre: creacion de orden, reduccion de stock y validacion de stock insuficiente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService - Tests Unitarios")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService service;

    @Nested
    @DisplayName("CREATE - Creacion de ordenes")
    class CreateOrderTests {

        @Test
        @DisplayName("Deberia lanzar excepcion si el cliente no existe")
        void shouldThrowWhenCustomerNotFound() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            OrderRequest request = new OrderRequest(99L, List.of(
                new OrderRequest.OrderItemRequest(1L, 2)
            ));

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deberia lanzar BusinessException por stock insuficiente")
        void shouldThrowWhenInsufficientStock() {
            // ARRANGE
            Customer customer = Customer.builder().id(1L).name("Ana").email("ana@email.com").build();
            Product product = Product.builder()
                    .id(1L).name("Laptop").price(999.0).stock(2).build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Intentar comprar 5 unidades cuando solo hay 2
            OrderRequest request = new OrderRequest(1L, List.of(
                new OrderRequest.OrderItemRequest(1L, 5)
            ));

            // ASSERT
            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Disponible");

            // El stock NO debe haberse reducido
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deberia crear la orden y reducir el stock del producto")
        void shouldCreateOrderAndReduceStock() {
            // ARRANGE
            Customer customer = Customer.builder().id(1L).name("Carlos").email("carlos@email.com").build();
            Product product = Product.builder()
                    .id(1L).name("Mouse").price(59.99).stock(10).build();

            Order savedOrder = Order.builder()
                    .id(1L).customer(customer).total(119.98).status(OrderStatus.PENDING).build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            OrderRequest request = new OrderRequest(1L, List.of(
                new OrderRequest.OrderItemRequest(1L, 2)
            ));

            // ACT
            OrderResponse result = service.create(request);

            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.customerId()).isEqualTo(1L);

            // Verificar que el stock fue reducido: save del producto debe haberse llamado
            verify(productRepository, atLeastOnce()).save(any(Product.class));
            // El stock del producto deberia haber bajado de 10 a 8
            assertThat(product.getStock()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("READ - Consulta de ordenes")
    class ReadOrderTests {

        @Test
        @DisplayName("Deberia lanzar excepcion si la orden no existe")
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Deberia lanzar excepcion al consultar ordenes de cliente inexistente")
        void shouldThrowWhenCustomerNotFoundForOrders() {
            when(customerRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.findByCustomerId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}



