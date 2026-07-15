package com.tacomoloco.reportes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteInventarioDTO {
    private Integer totalIngredientes;
    private Integer disponibles;
    private Integer stockBajo;
    private Integer agotados;
    private BigDecimal valorTotalInventario;
    private List<IngredienteStockDTO> detalle;

    @Data
    public static class IngredienteStockDTO {
        private Long id;
        private String nombre;
        private String unidadMedida;
        private Double cantidadDisponible;
        private Double stockMinimo;
        private String estado;
        private BigDecimal costoUnitario;
        private BigDecimal valorTotal;
        private Boolean alertaStockBajo;
    }
}