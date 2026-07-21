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

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "cantidad_disponible", nullable = false)
    private Double cantidadDisponible;

    @Column(name = "stock_minimo", nullable = false)
    private Double stockMinimo = 10.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoIngrediente estado;

    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    public enum EstadoIngrediente {
        DISPONIBLE, STOCK_BAJO, AGOTADO
    }

    public void actualizarEstado() {
        if (this.cantidadDisponible <= 0) {
            this.estado = EstadoIngrediente.AGOTADO;
        } else if (this.cantidadDisponible <= this.stockMinimo) {
            this.estado = EstadoIngrediente.STOCK_BAJO;
        } else {
            this.estado = EstadoIngrediente.DISPONIBLE;
        }
    }
}
