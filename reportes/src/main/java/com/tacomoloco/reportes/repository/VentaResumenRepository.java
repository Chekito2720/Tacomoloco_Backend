package com.tacomoloco.reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tacomoloco.reportes.entity.VentaResumen;

import java.time.LocalDate;
import java.util.List;


public interface VentaResumenRepository extends JpaRepository<VentaResumen, Long> {
    List<VentaResumen> findByFechaBetween(LocalDate inicio, LocalDate fin);

    List<VentaResumen> findByProductoId(Long productoId);
}
