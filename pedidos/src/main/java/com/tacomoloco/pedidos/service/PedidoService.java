package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.entity.*;
import com.tacomoloco.pedidos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final NotificacionRepository notificacionRepository;
    private final PersonalizacionIngredienteRepository personalizacionIngredienteRepository;

    public List<Pedido> obtenerPorFechaEntre(LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByFechaCreacionBetween(inicio, fin);
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> obtenerPorEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public DetallePedido agregarDetalle(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    @Transactional
    public PersonalizacionIngrediente agregarPersonalizacion(PersonalizacionIngrediente personalizacion) {
        return personalizacionIngredienteRepository.save(personalizacion);
    }

    @Transactional
    public Pago simularPago(Pedido pedido, String metodo) {
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodo(metodo);
        pago.setMonto(pedido.getTotal());
        pago.setEstado(Pago.EstadoPago.SIMULADO_EXITOSO);
        return pagoRepository.save(pago);
    }

    @Transactional
    public Notificacion crearNotificacion(Pedido pedido, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setPedido(pedido);
        notificacion.setClienteId(pedido.getClienteId());
        notificacion.setMensaje(mensaje);
        return notificacionRepository.save(notificacion);
    }

    public List<DetallePedido> obtenerDetalles(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId);
    }

    public Optional<Pago> obtenerPago(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId);
    }

    public List<Notificacion> obtenerNotificaciones(Long pedidoId) {
        return notificacionRepository.findByPedidoId(pedidoId);
    }

    @Transactional
    public Pedido actualizarEstado(Long pedidoId, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}