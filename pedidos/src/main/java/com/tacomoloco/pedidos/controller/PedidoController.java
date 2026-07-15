package com.tacomoloco.pedidos.controller;

import com.tacomoloco.pedidos.dto.CheckoutRequestDTO;
import com.tacomoloco.pedidos.dto.CheckoutResponseDTO;
import com.tacomoloco.pedidos.dto.PedidoAdminDTO;
import com.tacomoloco.pedidos.dto.PedidoResumenDTO;
import com.tacomoloco.pedidos.entity.*;
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
            @RequestParam(value = "inicio", required = false) LocalDateTime inicio,
            @RequestParam(value = "fin", required = false) LocalDateTime fin) {
        if (inicio != null && fin != null) {
            return ResponseEntity.ok(pedidoService.obtenerPorFechaEntre(inicio, fin));
        }
        return ResponseEntity.ok(pedidoService.obtenerTodos());
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
    public ResponseEntity<List<Pedido>> obtenerPorEstado(
            @PathVariable String estado,
            @RequestParam(value = "inicio", required = false) LocalDateTime inicio,
            @RequestParam(value = "fin", required = false) LocalDateTime fin) {
        Pedido.EstadoPedido estadoEnum = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
        if (inicio != null && fin != null) {
            return ResponseEntity.ok(pedidoService.obtenerPorEstadoYFecha(estadoEnum, inicio, fin));
        }
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estadoEnum));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoAdminDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Pedido.EstadoPedido estado,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado,
                usuarioId != null ? usuarioId : 0L));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<PedidoResumenDTO>> obtenerTodosAdmin(
            @RequestParam(value = "estado", required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            Pedido.EstadoPedido estadoEnum = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(pedidoService.obtenerResumenesPorEstado(estadoEnum));
        }
        return ResponseEntity.ok(pedidoService.obtenerResumenesTodos());
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<PedidoAdminDTO> obtenerDetalleAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoAdmin(id));
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

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoPedido>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialEstados(id));
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