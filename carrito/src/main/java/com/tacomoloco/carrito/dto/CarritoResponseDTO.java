package com.tacomoloco.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponseDTO {
    private Long id;
    private Long clienteId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private BigDecimal total;
    private Integer cantidadItems;
    private List<ItemCarritoResponseDTO> items;
    private List<GrupoPedidoResponseDTO> grupos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemCarritoResponseDTO {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private String imagenUrl;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private Long grupoPedidoId;
        private String grupoPedidoNombre;
        private List<PersonalizacionResponseDTO> personalizaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalizacionResponseDTO {
        private Long id;
        private Long ingredienteId;
        private String nombreIngrediente;
        private String tipo;
        private BigDecimal costoExtra;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrupoPedidoResponseDTO {
        private Long id;
        private String nombre;
        private String nota;
        private Integer orden;
        private BigDecimal subtotal;
        private List<ItemCarritoResponseDTO> items;
    }
}