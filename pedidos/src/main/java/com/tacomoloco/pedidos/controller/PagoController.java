package com.tacomoloco.pedidos.controller;

import com.tacomoloco.pedidos.dto.PagoResponseDTO;
import com.tacomoloco.pedidos.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerPagoPedido(pedidoId));
    }

    @PostMapping("/simular/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> simularPago(
            @PathVariable Long pedidoId,
            @RequestParam(value = "metodo", required = false) String metodo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.simularPagoPedido(pedidoId, metodo));
    }
}