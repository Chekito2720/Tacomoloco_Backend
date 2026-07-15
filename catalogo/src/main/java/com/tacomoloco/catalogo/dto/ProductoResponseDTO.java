package com.tacomoloco.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Boolean disponible;
    private String categoria;
    private List<IngredienteResponseDTO> ingredientes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredienteResponseDTO {
        private Long ingredienteId;
        private String nombreIngrediente;
        private String unidadMedida;
        private BigDecimal cantidadRequerida;
        private BigDecimal costoUnitario;
    }
}