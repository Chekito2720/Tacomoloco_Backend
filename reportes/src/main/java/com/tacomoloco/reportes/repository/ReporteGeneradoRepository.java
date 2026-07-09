package com.tacomoloco.reportes.repository;

import com.tacomoloco.reportes.model.entity.ReporteGenerado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteGeneradoRepository extends JpaRepository<ReporteGenerado, Long> {
    List<ReporteGenerado> findByTipo(ReporteGenerado.TipoReporte tipo);

    List<ReporteGenerado> findByGeneradoPor(Long usuarioId);
}
