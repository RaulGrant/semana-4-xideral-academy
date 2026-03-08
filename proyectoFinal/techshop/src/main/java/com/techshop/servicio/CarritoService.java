package com.techshop.servicio;

import com.techshop.modelo.CarritoItem;
import com.techshop.modelo.Producto;
import com.techshop.repositorio.CarritoItemRepository;
import com.techshop.repositorio.ProductoRepository;
import com.techshop.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio del carrito de compras.
 * Valida la existencia del usuario (MySQL) y del producto (MongoDB)
 * antes de agregar items. Permite agregar, quitar y consultar el carrito.
 * Si el producto ya esta en el carrito, actualiza la cantidad en lugar de duplicar.
 */
@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoItemRepository carritoItemRepository,
                          ProductoRepository productoRepository,
                          UsuarioRepository usuarioRepository) {
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Agrega un producto al carrito del usuario.
     * Valida que el usuario y el producto existan. Si el producto ya esta
     * en el carrito, suma la cantidad. Desnormaliza nombre y precio del producto
     * desde MongoDB para evitar consultas repetidas al mostrar el carrito.
     *
     * @param usuarioId  ID del usuario dueno del carrito
     * @param productoId ID del producto en MongoDB
     * @param cantidad   cantidad a agregar (debe ser >= 1)
     * @return el item del carrito creado o actualizado
     * @throws IllegalArgumentException si el usuario o producto no existen
     */
    public CarritoItem agregarItem(Long usuarioId, String productoId, int cantidad) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId);
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + productoId));

        // Si el producto ya esta en el carrito, actualizar cantidad
        Optional<CarritoItem> existente = carritoItemRepository
                .findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        }

        CarritoItem nuevoItem = new CarritoItem(
                usuarioId, productoId, producto.getNombre(),
                producto.getPrecio(), cantidad);
        return carritoItemRepository.save(nuevoItem);
    }

    /**
     * Elimina un item del carrito por su ID.
     *
     * @param itemId ID del item a eliminar
     * @throws IllegalArgumentException si el item no existe
     */
    public void eliminarItem(Long itemId) {
        if (!carritoItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item de carrito no encontrado con id: " + itemId);
        }
        carritoItemRepository.deleteById(itemId);
    }

    /**
     * Obtiene todos los items del carrito de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de items en el carrito (puede estar vacia)
     */
    public List<CarritoItem> obtenerCarrito(Long usuarioId) {
        return carritoItemRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Vacia completamente el carrito de un usuario.
     * Se usa despues de confirmar una orden.
     *
     * @param usuarioId ID del usuario cuyo carrito se vacia
     */
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        carritoItemRepository.deleteByUsuarioId(usuarioId);
    }
}
