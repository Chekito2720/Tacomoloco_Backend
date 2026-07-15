package com.tacomoloco.pedidos.controller;

import com.tacomoloco.pedidos.entity.Notificacion;
import com.tacomoloco.pedidos.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Notificacion>> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(notificacionService.obtenerPorPedido(pedidoId));
    }

    @GetMapping("/no-leidas/{clienteId}")
    public ResponseEntity<List<Notificacion>> obtenerNoLeidas(@PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(clienteId));
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @PatchMapping("/leer-todas/{clienteId}")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long clienteId) {
        notificacionService.marcarTodasComoLeidas(clienteId);
        return ResponseEntity.noContent().build();
    }
}