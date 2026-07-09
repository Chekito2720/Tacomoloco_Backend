package com.tacomoloco.pedidos.repository;

import com.tacomoloco.pedidos.model.entity.PersonalizacionIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalizacionIngredienteRepository extends JpaRepository<PersonalizacionIngrediente, Long> {
    List<PersonalizacionIngrediente> findByDetallePedidoId(Long detallePedidoId);
}
