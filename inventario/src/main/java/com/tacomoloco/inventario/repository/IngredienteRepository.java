package com.tacomoloco.inventario.repository;

import com.tacomoloco.inventario.model.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    List<Ingrediente> findByEstado(Ingrediente.EstadoIngrediente estado);
}
