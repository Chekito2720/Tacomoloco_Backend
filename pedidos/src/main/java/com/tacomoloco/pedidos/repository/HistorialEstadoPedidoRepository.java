package com.tacomoloco.pedidos.repository;

import com.tacomoloco.pedidos.entity.HistorialEstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoPedidoRepository extends JpaRepository<HistorialEstadoPedido, Long> {
    List<HistorialEstadoPedido> findByPedidoIdOrderByFechaCambioAsc(Long pedidoId);
}