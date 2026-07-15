package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.pedidos.entity.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
}
