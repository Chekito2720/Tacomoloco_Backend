package com.tacomoloco.carrito.repository;

import com.tacomoloco.carrito.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByClienteIdAndActivoTrue(Long clienteId);
}