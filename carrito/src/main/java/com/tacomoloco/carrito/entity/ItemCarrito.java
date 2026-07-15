package com.tacomoloco.carrito.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "item_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_pedido_id")
    private GrupoPedido grupoPedido;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "nombre_producto", nullable = false)
    private String nombreProducto;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @OneToMany(mappedBy = "itemCarrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPersonalizacion> personalizaciones;

    public BigDecimal calcularSubtotalConExtras() {
        BigDecimal base = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        if (personalizaciones == null || personalizaciones.isEmpty()) {
            return base;
        }
        BigDecimal extras = personalizaciones.stream()
                .map(ItemPersonalizacion::getCostoExtra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return base.add(extras);
    }
}