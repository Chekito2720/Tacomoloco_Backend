package com.tacomoloco.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String unidadMedida;

    @Column(nullable = false)
    private Double cantidadDisponible;

    @Column(name = "stock_minimo", nullable = false)
    private Double stockMinimo = 10.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIngrediente estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    public enum EstadoIngrediente {
        DISPONIBLE, AGOTADO
    }

    public void actualizarEstado() {
        this.estado = this.cantidadDisponible <= this.stockMinimo ? EstadoIngrediente.AGOTADO : EstadoIngrediente.DISPONIBLE;
    }
}