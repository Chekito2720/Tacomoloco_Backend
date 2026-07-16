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
public class PedidoSeguimientoDTO {
    private Long id;
    private String estado;
    private String estadoMensaje;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private String notasCliente;
    private Integer cantidadItems;
    private String estadoPago;
    private String estadoPagoMensaje;
    private List<ItemSeguimientoDTO> items;
    private List<HistorialSeguimientoDTO> historial;
    private List<NotificacionSeguimientoDTO> notificaciones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemSeguimientoDTO {
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal subtotal;
        private String grupoNombre;
        private List<String> personalizaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialSeguimientoDTO {
        private String mensaje;
        private LocalDateTime fecha;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificacionSeguimientoDTO {
        private Long id;
        private String mensaje;
        private LocalDateTime fecha;
        private Boolean leida;
    }
}