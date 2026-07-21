package com.tacomoloco.carrito.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grupo_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nota")
    private String nota;

    @Column(name = "orden", nullable = false)
    private Integer orden;
}