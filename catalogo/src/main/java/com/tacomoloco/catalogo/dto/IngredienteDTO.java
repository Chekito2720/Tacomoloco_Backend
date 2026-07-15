package com.tacomoloco.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteDTO {
    private Long id;
    private String nombre;
    private String unidadMedida;
    private Double cantidadDisponible;
    private String estado;
    private BigDecimal costoUnitario;
    private Boolean activo;

    public Boolean getActivo() {
        return "DISPONIBLE".equals(estado);
    }
}