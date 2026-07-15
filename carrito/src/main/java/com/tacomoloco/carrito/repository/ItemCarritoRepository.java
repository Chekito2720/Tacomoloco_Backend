package com.tacomoloco.carrito.repository;

import com.tacomoloco.carrito.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    List<ItemCarrito> findByCarritoId(Long carritoId);

    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
}