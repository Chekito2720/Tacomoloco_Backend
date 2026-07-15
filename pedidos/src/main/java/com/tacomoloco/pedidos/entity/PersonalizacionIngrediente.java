package com.tacomoloco.pedidos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "personalizacion_ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizacionIngrediente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_pedido_id", nullable = false)
    private DetallePedido detallePedido;

    @Column(name = "ingrediente_id", nullable = false)
    private Long ingredienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPersonalizacion tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoExtra;

    @Column(name = "nombre_ingrediente")
    private String nombreIngrediente;

    public enum TipoPersonalizacion {
        AGREGADO, QUITADO
    }
}
