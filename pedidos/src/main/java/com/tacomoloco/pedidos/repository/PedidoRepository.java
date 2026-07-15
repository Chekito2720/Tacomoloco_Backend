package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.pedidos.entity.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(Pedido.EstadoPedido estado);

    List<Pedido> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Pedido> findByEstadoAndFechaCreacionBetween(Pedido.EstadoPedido estado, LocalDateTime inicio, LocalDateTime fin);

    List<Pedido> findByClienteIdAndEstado(Long clienteId, Pedido.EstadoPedido estado);

    List<Pedido> findAllByOrderByFechaCreacionDesc();
}
