package com.tacomoloco.carrito.repository;

import com.tacomoloco.carrito.entity.GrupoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoPedidoRepository extends JpaRepository<GrupoPedido, Long> {
    List<GrupoPedido> findByCarritoIdOrderByOrdenAsc(Long carritoId);
}