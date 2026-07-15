package com.tacomoloco.catalogo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "producto_ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoIngrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "ingrediente_id", nullable = false)
    private Long ingredienteId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadRequerida;
}
