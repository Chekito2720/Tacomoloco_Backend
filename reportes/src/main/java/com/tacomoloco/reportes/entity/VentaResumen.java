package com.tacomoloco.reportes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "venta_resumen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResumen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidadVendida;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVenta;
}
