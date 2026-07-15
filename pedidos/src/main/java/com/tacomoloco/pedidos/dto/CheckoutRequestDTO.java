package com.tacomoloco.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO {
    private Long clienteId;
    private String notasCliente;
    private BigDecimal total;
    private List<DetalleCheckoutDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleCheckoutDTO {
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private Long grupoPedidoId;
        private String grupoNombre;
        private String grupoNota;
        private List<PersonalizacionCheckoutDTO> personalizaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalizacionCheckoutDTO {
        private Long ingredienteId;
        private String tipo;
        private BigDecimal costoExtra;
        private String nombreIngrediente;
    }
}