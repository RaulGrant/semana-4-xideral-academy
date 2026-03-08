package com.tienda.service;

import com.tienda.dto.ProductRequest;
import com.tienda.dto.ProductResponse;
import com.tienda.exception.BusinessException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Category;
import com.tienda.model.Product;
import com.tienda.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EJERCICIO 4 - Tests unitarios del ProductService con JUnit 5 y Mockito.
 *
 * Conceptos fundamentales:
 *
 * @ExtendWith(MockitoExtension.class): integra Mockito con JUnit 5.
 *   Activa la inicializacion automatica de @Mock e @InjectMocks.
 *
 * @Mock: crea un objeto simulado (doble de prueba) del tipo indicado.
 *   Todos sus metodos retornan valores por defecto (null, 0, false) a menos
 *   que se especifique un comportamiento con when().
 *
 * @InjectMocks: crea la instancia real de la clase bajo prueba (CUT - Class
 *   Under Test) e inyecta los @Mock en su constructor/campos automaticamente.
 *
 * when(...).thenReturn(...): define el comportamiento del mock para una
 *   llamada especifica. Permite simular respuestas de la BD sin necesitarla.
 *
 * verify(...): comprueba que un metodo del mock fue llamado con los argumentos
 *   esperados. Si no se llamo, el test falla.
 *
 * ArgumentCaptor: captura el argumento real que se paso a un mock para
 *   inspeccionarlo con assertions detalladas.
 *
 * @Nested: organiza tests relacionados en clases internas para mejor legibilidad.
 *   Ideal para agrupar por operacion: Create, Read, Update, Delete.
 *
 * AssertJ (assertThat): libreria de assertions fluida incluida en spring-boot-starter-test.
 *   Mas expresiva que JUnit assertions nativas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Tests Unitarios")
class ProductServiceTest {

    // Mock del repositorio: simula la BD sin conectarse a ninguna BD real
    @Mock
    private ProductRepository repository;

    // Instancia real del servicio con el mock inyectado
    @InjectMocks
    private ProductService service;

    // ---------------------------------------------------------------
    // Helpers para crear objetos de prueba reutilizables
    // ---------------------------------------------------------------
    private Product buildProduct(Long id, String name, Double price, Category cat, Integer stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .category(cat)
                .stock(stock)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ProductRequest buildRequest(String name, Double price, Category cat, Integer stock) {
        return new ProductRequest(name, price, "Descripcion de prueba", cat, stock);
    }

    // ===============================================================
    @Nested
    @DisplayName("READ - Consultas de productos")
    class ReadTests {

        @Test
        @DisplayName("Deberia retornar lista de todos los productos")
        void shouldReturnAllProducts() {
            // ARRANGE: preparar datos y configurar comportamiento del mock
            List<Product> products = List.of(
                buildProduct(1L, "Laptop", 999.0, Category.ELECTRONICS, 10),
                buildProduct(2L, "Libro Java", 45.0, Category.BOOKS, 50)
            );
            when(repository.findAll()).thenReturn(products);

            // ACT: ejecutar el metodo bajo prueba
            List<ProductResponse> result = service.findAll();

            // ASSERT: verificar el resultado
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Laptop");
            assertThat(result.get(1).name()).isEqualTo("Libro Java");

            // Verificar que el repositorio fue consultado exactamente 1 vez
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deberia retornar un producto por ID cuando existe")
        void shouldReturnProductById() {
            // ARRANGE
            Product product = buildProduct(1L, "Laptop Pro", 1299.0, Category.ELECTRONICS, 25);
            when(repository.findById(1L)).thenReturn(Optional.of(product));

            // ACT
            ProductResponse result = service.findById(1L);

            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Laptop Pro");
            assertThat(result.price()).isEqualTo(1299.0);
            verify(repository).findById(1L);
        }

        @Test
        @DisplayName("Deberia lanzar ResourceNotFoundException cuando el producto no existe")
        void shouldThrowWhenProductNotFound() {
            // ARRANGE: simular que el repositorio no encuentra el producto
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // ASSERT: verificar que lanza la excepcion esperada
            // assertThatThrownBy es la forma recomendada en AssertJ
            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository).findById(99L);
        }

        @Test
        @DisplayName("Deberia retornar productos paginados")
        void shouldReturnPagedProducts() {
            // ARRANGE
            List<Product> products = List.of(
                buildProduct(1L, "Laptop", 999.0, Category.ELECTRONICS, 5)
            );
            Page<Product> page = new PageImpl<>(products);
            when(repository.findAll(any(PageRequest.class))).thenReturn(page);

            // ACT
            Page<ProductResponse> result = service.findAll(PageRequest.of(0, 10));

            // ASSERT
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Deberia buscar productos por nombre case-insensitive")
        void shouldSearchByName() {
            // ARRANGE
            List<Product> products = List.of(
                buildProduct(1L, "Laptop Gaming", 850.0, Category.ELECTRONICS, 15)
            );
            when(repository.findByNameContainingIgnoreCase("laptop")).thenReturn(products);

            // ACT
            List<ProductResponse> result = service.searchByName("laptop");

            // ASSERT
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Laptop Gaming");
            verify(repository).findByNameContainingIgnoreCase("laptop");
        }
    }

    // ===============================================================
    @Nested
    @DisplayName("CREATE - Creacion de productos")
    class CreateTests {

        @Test
        @DisplayName("Deberia crear un producto exitosamente")
        void shouldCreateProduct() {
            // ARRANGE
            ProductRequest request = buildRequest("Nuevo Producto", 99.99, Category.ELECTRONICS, 20);
            when(repository.existsByNameIgnoreCase("Nuevo Producto")).thenReturn(false);

            Product saved = buildProduct(1L, "Nuevo Producto", 99.99, Category.ELECTRONICS, 20);
            when(repository.save(any(Product.class))).thenReturn(saved);

            // ACT
            ProductResponse result = service.create(request);

            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Nuevo Producto");
            assertThat(result.price()).isEqualTo(99.99);
        }

        @Test
        @DisplayName("Deberia lanzar BusinessException si el nombre ya existe")
        void shouldRejectDuplicateName() {
            // ARRANGE: simular que ya existe un producto con ese nombre
            ProductRequest request = buildRequest("Laptop Pro", 999.0, Category.ELECTRONICS, 10);
            when(repository.existsByNameIgnoreCase("Laptop Pro")).thenReturn(true);

            // ASSERT: verificar que lanza la excepcion de conflicto
            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Laptop Pro");

            // Verificar que save() NUNCA fue llamado (no se persiste si hay error)
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deberia guardar el producto con los datos correctos (ArgumentCaptor)")
        void shouldSaveProductWithCorrectData() {
            // ARRANGE
            ProductRequest request = buildRequest("Auriculares BT", 79.99, Category.ELECTRONICS, 30);
            when(repository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(repository.save(any(Product.class)))
                    .thenAnswer(inv -> {
                        Product p = inv.getArgument(0);
                        p.setId(10L);
                        return p;
                    });

            // ACT
            service.create(request);

            // ArgumentCaptor captura el objeto Product que se paso a repository.save()
            // para inspeccionarlo con assertions detalladas
            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(repository).save(captor.capture());

            Product captured = captor.getValue();
            assertThat(captured.getName()).isEqualTo("Auriculares BT");
            assertThat(captured.getPrice()).isEqualTo(79.99);
            assertThat(captured.getStock()).isEqualTo(30);
            assertThat(captured.getCategory()).isEqualTo(Category.ELECTRONICS);
        }
    }

    // ===============================================================
    @Nested
    @DisplayName("UPDATE - Actualizacion de productos")
    class UpdateTests {

        @Test
        @DisplayName("Deberia actualizar un producto existente")
        void shouldUpdateProduct() {
            // ARRANGE
            Product existing = buildProduct(1L, "Laptop Vieja", 800.0, Category.ELECTRONICS, 5);
            ProductRequest request = buildRequest("Laptop Nueva", 950.0, Category.ELECTRONICS, 10);

            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.existsByNameIgnoreCase("Laptop Nueva")).thenReturn(false);
            when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            // ACT
            ProductResponse result = service.update(1L, request);

            // ASSERT
            assertThat(result.name()).isEqualTo("Laptop Nueva");
            assertThat(result.price()).isEqualTo(950.0);
            verify(repository).save(any(Product.class));
        }

        @Test
        @DisplayName("Deberia lanzar excepcion al actualizar producto inexistente")
        void shouldThrowWhenUpdatingNonExistentProduct() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, buildRequest("X", 1.0, Category.BOOKS, 1)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ===============================================================
    @Nested
    @DisplayName("DELETE - Eliminacion de productos")
    class DeleteTests {

        @Test
        @DisplayName("Deberia eliminar un producto existente")
        void shouldDeleteProduct() {
            // ARRANGE
            when(repository.existsById(1L)).thenReturn(true);
            doNothing().when(repository).deleteById(1L);

            // ACT
            service.delete(1L);

            // ASSERT: verificar que deleteById fue llamado con el ID correcto
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("Deberia lanzar excepcion al eliminar producto inexistente")
        void shouldThrowWhenDeletingNonExistentProduct() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            // Verificar que deleteById nunca se llama si el producto no existe
            verify(repository, never()).deleteById(any());
        }
    }
}

