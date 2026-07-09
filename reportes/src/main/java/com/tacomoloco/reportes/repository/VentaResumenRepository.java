package com.tacomoloco.reportes.repository;

import com.tacomoloco.reportes.model.entity.VentaResumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaResumenRepository extends JpaRepository<VentaResumen, Long> {
    List<VentaResumen> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<VentaResumen> findByProductoId(Long productoId);
}
