package com.tacomoloco.reportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tacomoloco.reportes.entity.ReporteGenerado;

import java.util.List;


public interface ReporteGeneradoRepository extends JpaRepository<ReporteGenerado, Long> {
    List<ReporteGenerado> findByTipo(ReporteGenerado.TipoReporte tipo);

    List<ReporteGenerado> findByGeneradoPor(Long usuarioId);
}
