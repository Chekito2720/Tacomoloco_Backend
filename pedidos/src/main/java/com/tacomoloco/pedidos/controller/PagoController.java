package com.tacomoloco.pedidos.controller;

import com.tacomoloco.pedidos.entity.Pago;
import com.tacomoloco.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PedidoService pedidoService;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pago> obtenerPagoPorPedido(@PathVariable Long pedidoId) {
        return pedidoService.obtenerPago(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}