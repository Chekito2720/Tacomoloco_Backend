package com.tacomoloco.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tacomoloco.pedidos.entity.PersonalizacionIngrediente;

import java.util.List;


public interface PersonalizacionIngredienteRepository extends JpaRepository<PersonalizacionIngrediente, Long> {
    List<PersonalizacionIngrediente> findByDetallePedidoId(Long detallePedidoId);
}
