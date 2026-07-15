package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tacomoloco.pedidos.entity.Pago;

import java.util.Optional;


public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByPedidoId(Long pedidoId);
}
