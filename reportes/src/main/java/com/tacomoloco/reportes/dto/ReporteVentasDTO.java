package com.tacomoloco.reportes.dto;

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
public class ReporteVentasDTO {
    private String periodo;
    private BigDecimal totalCompras;
    private Integer totalPedidos;
    private List<ProductoTopDTO> topProductos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoTopDTO {
        private Long productoId;
        private String productoNombre;
        private String categoria;
        private Integer cantidadTotal;
        private BigDecimal totalVendido;
        private Integer posicion;
    }
}