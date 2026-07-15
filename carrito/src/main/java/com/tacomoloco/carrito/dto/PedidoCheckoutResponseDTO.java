package com.tacomoloco.carrito.dto;

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
public class PedidoCheckoutResponseDTO {
    private Long pedidoId;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private String estadoPago;
}