package com.tacomoloco.pedidos.controller;

import com.tacomoloco.pedidos.dto.CheckoutRequestDTO;
import com.tacomoloco.pedidos.dto.CheckoutResponseDTO;
import com.tacomoloco.pedidos.entity.DetallePedido;
import com.tacomoloco.pedidos.entity.Notificacion;
import com.tacomoloco.pedidos.entity.Pago;
import com.tacomoloco.pedidos.entity.Pedido;
import com.tacomoloco.pedidos.service.NotificacionService;
import com.tacomoloco.pedidos.service.PagoService;
import com.tacomoloco.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PagoService pagoService;
    private final NotificacionService notificacionService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckoutRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarCheckout(request));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerPorFechaEntre(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fin") LocalDateTime fin) {
        return ResponseEntity.ok(pedidoService.obtenerPorFechaEntre(inicio, fin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> obtenerPorEstado(@PathVariable String estado) {
        Pedido.EstadoPedido estadoEnum = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estadoEnum));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam Pedido.EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetallePedido>> obtenerDetalles(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerDetalles(id));
    }

    @GetMapping("/{id}/pago")
    public ResponseEntity<Pago> obtenerPago(@PathVariable Long id) {
        return pedidoService.obtenerPago(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/notificaciones")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerNotificaciones(id));
    }

    @GetMapping("/notificaciones/no-leidas/{clienteId}")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesNoLeidas(@PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(clienteId));
    }

    @PatchMapping("/notificaciones/{notificacionId}/leida")
    public ResponseEntity<Notificacion> marcarNotificacionLeida(@PathVariable Long notificacionId) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(notificacionId));
    }

    @PatchMapping("/notificaciones/leer-todas/{clienteId}")
    public ResponseEntity<Void> marcarTodasLeidas(@PathVariable Long clienteId) {
        notificacionService.marcarTodasComoLeidas(clienteId);
        return ResponseEntity.noContent().build();
    }
}