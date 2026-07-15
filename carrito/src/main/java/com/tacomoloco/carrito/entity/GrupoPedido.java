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

    @Column
    private String nombre;

    @Column
    private String nota;

    @Column(nullable = false)
    private Integer orden;
}