package com.techshop.repositorio;

import com.techshop.modelo.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByUsuarioId(Long usuarioId);

    Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, String productoId);

    void deleteByUsuarioId(Long usuarioId);
}
