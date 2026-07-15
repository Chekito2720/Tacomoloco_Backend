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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIngrediente estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    public enum EstadoIngrediente {
        DISPONIBLE, AGOTADO
    }
}
