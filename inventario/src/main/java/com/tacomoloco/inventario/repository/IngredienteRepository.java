package com.tacomoloco.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tacomoloco.inventario.entity.Ingrediente;

import java.util.List;


public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    List<Ingrediente> findByEstado(Ingrediente.EstadoIngrediente estado);
}
