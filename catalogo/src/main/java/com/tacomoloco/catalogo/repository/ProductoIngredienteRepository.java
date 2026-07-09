package com.tacomoloco.catalogo.repository;

import com.tacomoloco.catalogo.model.entity.ProductoIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngrediente, Long> {
    List<ProductoIngrediente> findByProductoId(Long productoId);
}
