package com.tacomoloco.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.catalogo.entity.ProductoIngrediente;

import java.util.List;


public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngrediente, Long> {
    List<ProductoIngrediente> findByProductoId(Long productoId);
}
