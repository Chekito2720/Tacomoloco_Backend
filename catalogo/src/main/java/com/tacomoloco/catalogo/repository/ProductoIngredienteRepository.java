package com.tacomoloco.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.tacomoloco.catalogo.entity.ProductoIngrediente;

import java.util.List;


public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngrediente, Long> {
    List<ProductoIngrediente> findByProductoId(Long productoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductoIngrediente pi WHERE pi.producto.id = :productoId")
    void deleteByProductoId(Long productoId);
}
