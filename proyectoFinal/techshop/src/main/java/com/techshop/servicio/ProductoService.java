package com.techshop.servicio;

import com.techshop.modelo.Producto;
import com.techshop.repositorio.CategoriaRepository;
import com.techshop.repositorio.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio para la gestion de productos del catalogo.
 * Los productos se almacenan en MongoDB con esquema flexible por categoria.
 * Valida que la categoria exista antes de crear o actualizar un producto.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(String id) {
        return productoRepository.findById(id);
    }

    /**
     * Filtra productos por nombre de categoria.
     * Usado por el cliente para navegar el catalogo (RF002).
     */
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    /**
     * Crea un producto validando que la categoria asignada exista en el sistema.
     * @throws IllegalArgumentException si la categoria no existe
     */
    public Producto crear(Producto producto) {
        validarCategoriaExiste(producto.getCategoria());
        return productoRepository.save(producto);
    }

    /**
     * Actualiza todos los campos de un producto existente.
     * Valida que el producto exista y que la nueva categoria (si cambio) tambien exista.
     * @throws IllegalArgumentException si el producto o la categoria no existen
     */
    public Producto actualizar(String id, Producto producto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        validarCategoriaExiste(producto.getCategoria());
        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());
        existente.setImagenUrl(producto.getImagenUrl());
        existente.setCategoria(producto.getCategoria());
        existente.setAtributos(producto.getAtributos());
        return productoRepository.save(existente);
    }

    /**
     * Elimina un producto. Valida existencia antes de borrar.
     * @throws IllegalArgumentException si el producto no existe
     */
    public void eliminar(String id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    private void validarCategoriaExiste(String nombreCategoria) {
        if (categoriaRepository.findByNombre(nombreCategoria).isEmpty()) {
            throw new IllegalArgumentException("La categoria no existe: " + nombreCategoria);
        }
    }
}
