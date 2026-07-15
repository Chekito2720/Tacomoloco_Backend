package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tacomoloco.pedidos.entity.DetallePedido;

import java.util.List;


public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedidoId(Long pedidoId);
}
