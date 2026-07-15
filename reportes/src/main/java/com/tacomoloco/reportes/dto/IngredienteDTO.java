package com.tacomoloco.reportes.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredienteDTO {
    private Long id;
    private String nombre;
    private String unidadMedida;
    private Double cantidadDisponible;
    private Double stockMinimo;
    private String estado;
    private BigDecimal costoUnitario;
}