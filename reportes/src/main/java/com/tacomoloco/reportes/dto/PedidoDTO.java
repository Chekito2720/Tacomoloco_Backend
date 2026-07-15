package com.tacomoloco.reportes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private String estado;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private String notasCliente;
    private List<DetallePedidoDTO> detalles;
}