package com.tacomoloco.pedidos.dto;

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
public class PedidoAdminDTO {
    private Long id;
    private Long clienteId;
    private String estado;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private String notasCliente;
    private Integer cantidadItems;
    private String estadoPago;
    private List<DetalleAdminDTO> detalles;
    private List<HistorialEstadoDTO> historial;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleAdminDTO {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String grupoNombre;
        private String grupoNota;
        private List<PersonalizacionAdminDTO> personalizaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalizacionAdminDTO {
        private Long ingredienteId;
        private String nombreIngrediente;
        private String tipo;
        private BigDecimal costoExtra;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialEstadoDTO {
        private String estadoAnterior;
        private String estadoNuevo;
        private LocalDateTime fechaCambio;
    }
}