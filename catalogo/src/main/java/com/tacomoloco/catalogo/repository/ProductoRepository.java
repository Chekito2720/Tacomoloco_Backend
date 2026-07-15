package com.tacomoloco.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.catalogo.entity.Producto;

import java.util.List;


public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoria(String categoria);
}
