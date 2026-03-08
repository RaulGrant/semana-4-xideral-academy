package com.techshop.servicio;

import com.techshop.modelo.CarritoItem;
import com.techshop.modelo.Orden;
import com.techshop.modelo.OrdenDetalle;
import com.techshop.repositorio.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica de negocio para la creacion y consulta de ordenes (pedidos).
 * Convierte el carrito del usuario en una orden con sus detalles,
 * calcula el total y vacia el carrito despues de confirmar.
 * Integra MySQL (ordenes, carrito, usuarios) con MongoDB (productos).
 */
@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final CarritoService carritoService;

    public OrdenService(OrdenRepository ordenRepository, CarritoService carritoService) {
        this.ordenRepository = ordenRepository;
        this.carritoService = carritoService;
    }

    /**
     * Crea una orden a partir del carrito del usuario.
     * Toma todos los items del carrito, los convierte en lineas de detalle,
     * calcula el total, persiste la orden y vacia el carrito.
     *
     * @param usuarioId ID del usuario que realiza el pedido
     * @return la orden creada con sus detalles
     * @throws IllegalArgumentException si el carrito esta vacio
     */
    @Transactional
    public Orden crearOrden(Long usuarioId) {
        List<CarritoItem> items = carritoService.obtenerCarrito(usuarioId);

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "El carrito esta vacio. Agrega productos antes de crear una orden.");
        }

        Orden orden = new Orden();
        orden.setUsuarioId(usuarioId);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado("PENDIENTE");

        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem item : items) {
            BigDecimal subtotal = item.getPrecioUnitario()
                    .multiply(new BigDecimal(item.getCantidad()));

            OrdenDetalle detalle = new OrdenDetalle(
                    item.getProductoId(),
                    item.getNombreProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    subtotal);

            orden.agregarDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total);
        Orden ordenGuardada = ordenRepository.save(orden);

        // Vaciar el carrito despues de crear la orden
        carritoService.vaciarCarrito(usuarioId);

        return ordenGuardada;
    }

    /**
     * Busca una orden por su ID.
     *
     * @param id identificador de la orden
     * @return la orden con sus detalles
     * @throws IllegalArgumentException si la orden no existe
     */
    public Orden buscarPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con id: " + id));
    }

    /**
     * Lista todas las ordenes de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return lista de ordenes (puede estar vacia)
     */
    public List<Orden> listarPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }
}
