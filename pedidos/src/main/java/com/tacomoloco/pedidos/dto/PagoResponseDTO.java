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
public class PagoResponseDTO {
    private Long id;
    private Long pedidoId;
    private String metodo;
    private BigDecimal monto;
    private String estado;
    private String estadoMensaje;
    private LocalDateTime fecha;
}