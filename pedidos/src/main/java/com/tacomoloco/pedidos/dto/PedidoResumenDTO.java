package com.tacomoloco.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResumenDTO {
    private Long id;
    private Long clienteId;
    private String estado;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private Integer cantidadItems;
    private String estadoPago;
}