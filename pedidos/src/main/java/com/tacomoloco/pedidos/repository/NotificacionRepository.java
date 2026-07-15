package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tacomoloco.pedidos.entity.Notificacion;

import java.util.List;


public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByPedidoId(Long pedidoId);

    List<Notificacion> findByClienteIdAndLeidoFalse(Long clienteId);
}
