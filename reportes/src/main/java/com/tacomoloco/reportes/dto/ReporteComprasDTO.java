package com.tacomoloco.reportes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReporteComprasDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalCompras;
    private Integer totalPedidos;
    private List<ResumenProductoDTO> productosMasVendidos;
    private List<IngredienteConsumidoDTO> ingredientesConsumidos;

    @Data
    public static class ResumenProductoDTO {
        private Long productoId;
        private String productoNombre;
        private Integer cantidadTotal;
        private BigDecimal totalVendido;
    }

    @Data
    public static class IngredienteConsumidoDTO {
        private Long ingredienteId;
        private String ingredienteNombre;
        private Double cantidadConsumida;
        private String unidadMedida;
        private BigDecimal costoTotal;
    }
}