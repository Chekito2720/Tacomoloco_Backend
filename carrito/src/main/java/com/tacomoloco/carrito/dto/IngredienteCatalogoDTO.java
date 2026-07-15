package com.tacomoloco.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteCatalogoDTO {
    private Long ingredienteId;
    private String nombreIngrediente;
    private String unidadMedida;
    private BigDecimal cantidadRequerida;
    private BigDecimal costoUnitario;
}