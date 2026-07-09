package com.tacomoloco.reportes.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_generado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGenerado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(columnDefinition = "TEXT")
    private String parametros;

    @Column(nullable = false)
    private Long generadoPor;

    public enum TipoReporte {
        COMPRAS, INGREDIENTES, VENTAS, RENDIMIENTO
    }

    @PrePersist
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
}
