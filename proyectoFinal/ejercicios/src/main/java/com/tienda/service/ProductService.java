package com.tienda.service;

import com.tienda.dto.ProductRequest;
import com.tienda.dto.ProductResponse;
import com.tienda.exception.BusinessException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Category;
import com.tienda.model.Product;
import com.tienda.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EJERCICIOS 2, 3 y 6 - Capa de servicio para productos.
 *
 * @Service: marca el bean como componente de capa de negocio.
 * @Transactional: garantiza que los metodos de escritura se ejecuten
 *   dentro de una transaccion. Si ocurre una excepcion, se hace rollback.
 * @RequiredArgsConstructor (Lombok): genera constructor con los campos
 *   final, permitiendo inyeccion de dependencias por constructor
 *   (la forma recomendada frente a @Autowired en campo).
 *
 * La capa de servicio NO debe saber nada de HTTP. Es independiente del
 * controlador y puede ser testeada sin levantar el contexto web.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    /** Retorna todos los productos como lista de DTOs. */
    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    /**
     * EJERCICIO 6 - Retorna productos paginados.
     * Pageable encapsula: numero de pagina, tamanio y criterio de ordenacion.
     */
    public Page<ProductResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProductResponse::fromEntity);
    }

    /** Busca un producto por ID o lanza ResourceNotFoundException (404). */
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return ProductResponse.fromEntity(product);
    }

    /** Busca productos por fragmento de nombre (case-insensitive). */
    public List<ProductResponse> searchByName(String keyword) {
        return repository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    /** Filtra productos por categoria. */
    public List<ProductResponse> findByCategory(Category category) {
        return repository.findByCategory(category)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    /** Retorna solo productos con stock disponible. */
    public List<ProductResponse> findAvailable() {
        return repository.findAvailableProducts()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    /**
     * Crea un nuevo producto.
     * Valida que no exista otro con el mismo nombre para evitar duplicados.
     * @Transactional sin readOnly para permitir escritura.
     */
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Ya existe un producto con el nombre: " + request.name());
        }
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .category(request.category())
                .stock(request.stock() != null ? request.stock() : 0)
                .build();
        return ProductResponse.fromEntity(repository.save(product));
    }

    /**
     * Actualiza un producto existente.
     * Primero verifica que exista, luego aplica los cambios y guarda.
     */
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        // Valida nombre duplicado solo si el nombre cambia
        if (!product.getName().equalsIgnoreCase(request.name())
                && repository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Ya existe un producto con el nombre: " + request.name());
        }

        product.setName(request.name());
        product.setPrice(request.price());
        product.setDescription(request.description());
        product.setCategory(request.category());
        if (request.stock() != null) {
            product.setStock(request.stock());
        }
        return ProductResponse.fromEntity(repository.save(product));
    }

    /** Elimina un producto por ID. Lanza 404 si no existe. */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        repository.deleteById(id);
    }

    /** Top 5 productos mas vendidos (Ejercicio 5). */
    public List<ProductResponse> findBestSellers() {
        return repository.findTop5BestSellers()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}

