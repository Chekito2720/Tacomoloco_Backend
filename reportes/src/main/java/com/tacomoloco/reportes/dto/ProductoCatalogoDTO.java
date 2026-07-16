package com.tacomoloco.reportes.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoCatalogoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String categoria;
}