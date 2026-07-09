package com.tacomoloco.catalogo.repository;

import com.tacomoloco.catalogo.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoria(String categoria);
}
