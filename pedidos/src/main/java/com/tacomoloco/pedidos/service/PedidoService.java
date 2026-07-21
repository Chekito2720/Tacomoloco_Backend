package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.dto.*;
import com.tacomoloco.pedidos.entity.*;
import com.tacomoloco.pedidos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final NotificacionRepository notificacionRepository;
    private final PersonalizacionIngredienteRepository personalizacionIngredienteRepository;
    private final HistorialEstadoPedidoRepository historialEstadoPedidoRepository;

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

    public List<Pedido> obtenerPorEstadoYFecha(Pedido.EstadoPedido estado, LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByEstadoAndFechaCreacionBetween(estado, inicio, fin);
    }

    public List<Pedido> obtenerPorClienteYEstado(Long clienteId, Pedido.EstadoPedido estado) {
        return pedidoRepository.findByClienteIdAndEstado(clienteId, estado);
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaCreacionDesc();
    }

    public List<PedidoResumenDTO> obtenerResumenes() {
        return pedidoRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    public List<PedidoResumenDTO> obtenerResumenesPorEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::convertirAResumen)
                .collect(Collectors.toList());
    }

    public List<PedidoResumenDTO> obtenerResumenesTodos() {
        return obtenerResumenes();
    }

    public PedidoAdminDTO obtenerPedidoAdmin(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        return convertirAAdminDTO(pedido);
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
        pago.setEstado(Pago.EstadoPago.COMPLETADO);
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

    @Transactional
    public void crearNotificacionCliente(Long clienteId, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setClienteId(clienteId);
        notificacion.setMensaje(mensaje);
        notificacion.setLeido(false);
        notificacionRepository.save(notificacion);
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

    public List<HistorialEstadoPedido> obtenerHistorial(Long pedidoId) {
        return historialEstadoPedidoRepository.findByPedidoIdOrderByFechaCambioAsc(pedidoId);
    }

    @Transactional
    public PedidoAdminDTO actualizarEstado(Long pedidoId, Pedido.EstadoPedido nuevoEstado, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        String estadoAnterior = pedido.getEstado().name();

        HistorialEstadoPedido historial = new HistorialEstadoPedido();
        historial.setPedido(pedido);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado.name());
        historial.setUsuarioId(usuarioId);
        historialEstadoPedidoRepository.save(historial);

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);

        String mensaje = "Tu pedido #" + pedido.getId() + " ahora esta: " + traducirEstado(nuevoEstado);
        Notificacion notificacion = new Notificacion();
        notificacion.setPedido(pedido);
        notificacion.setClienteId(pedido.getClienteId());
        notificacion.setMensaje(mensaje);
        notificacionRepository.save(notificacion);

        return convertirAAdminDTO(pedido);
    }

    @Transactional
    public PedidoAdminDTO cancelarPedido(Long pedidoId, Long clienteId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        if (!pedido.getClienteId().equals(clienteId)) {
            throw new RuntimeException("Solo el cliente dueno del pedido puede cancelarlo");
        }

        if (pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO || pedido.getEstado() == Pedido.EstadoPedido.CANCELADO) {
            throw new RuntimeException("No se puede cancelar un pedido en estado " + pedido.getEstado().name());
        }

        String estadoAnterior = pedido.getEstado().name();

        HistorialEstadoPedido historial = new HistorialEstadoPedido();
        historial.setPedido(pedido);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(Pedido.EstadoPedido.CANCELADO.name());
        historial.setUsuarioId(clienteId);
        historialEstadoPedidoRepository.save(historial);

        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        Notificacion notificacion = new Notificacion();
        notificacion.setPedido(pedido);
        notificacion.setClienteId(pedido.getClienteId());
        notificacion.setMensaje("Has cancelado tu pedido #" + pedido.getId() + ". Si fue un error, haz un nuevo pedido.");
        notificacionRepository.save(notificacion);

        return convertirAAdminDTO(pedido);
    }

    public PedidoSeguimientoDTO obtenerSeguimiento(Long pedidoId, Long clienteId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        if (!pedido.getClienteId().equals(clienteId)) {
            throw new RuntimeException("No tienes acceso a este pedido");
        }

        return convertirASeguimientoDTO(pedido);
    }

    public List<HistorialEstadoPedido> obtenerHistorialEstados(Long pedidoId) {
        return historialEstadoPedidoRepository.findByPedidoIdOrderByFechaCambioAsc(pedidoId);
    }

    private PedidoResumenDTO convertirAResumen(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());
        Optional<Pago> pago = pagoRepository.findByPedidoId(pedido.getId());

        return PedidoResumenDTO.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .estado(pedido.getEstado().name())
                .fechaCreacion(pedido.getFechaCreacion())
                .total(pedido.getTotal())
                .cantidadItems(detalles.size())
                .estadoPago(pago.map(p -> p.getEstado().name()).orElse("SIN_PAGO"))
                .build();
    }

    private PedidoAdminDTO convertirAAdminDTO(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());
        Optional<Pago> pago = pagoRepository.findByPedidoId(pedido.getId());
        List<HistorialEstadoPedido> historial = historialEstadoPedidoRepository.findByPedidoIdOrderByFechaCambioAsc(pedido.getId());

        return PedidoAdminDTO.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .estado(pedido.getEstado().name())
                .fechaCreacion(pedido.getFechaCreacion())
                .total(pedido.getTotal())
                .notasCliente(pedido.getNotasCliente())
                .cantidadItems(detalles.size())
                .estadoPago(pago.map(p -> p.getEstado().name()).orElse("SIN_PAGO"))
                .detalles(detalles.stream().map(d -> {
                    List<PersonalizacionIngrediente> pers =
                            personalizacionIngredienteRepository.findByDetallePedidoId(d.getId());

                    return PedidoAdminDTO.DetalleAdminDTO.builder()
                            .id(d.getId())
                            .productoId(d.getProductoId())
                            .nombreProducto(d.getNombreProducto() != null ? d.getNombreProducto() : "Producto #" + d.getProductoId())
                            .cantidad(d.getCantidad())
                            .precioUnitario(d.getPrecioUnitario())
                            .subtotal(d.getSubtotal())
                            .grupoNombre(d.getGrupoNombre())
                            .grupoNota(d.getGrupoNota())
                            .personalizaciones(pers.stream()
                                    .map(p -> PedidoAdminDTO.PersonalizacionAdminDTO.builder()
                                            .ingredienteId(p.getIngredienteId())
                                            .nombreIngrediente(p.getNombreIngrediente() != null ? p.getNombreIngrediente() : "Ingrediente #" + p.getIngredienteId())
                                            .tipo(p.getTipo().name())
                                            .costoExtra(p.getCostoExtra())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();
                }).collect(Collectors.toList()))
                .historial(historial.stream()
                        .map(h -> PedidoAdminDTO.HistorialEstadoDTO.builder()
                                .estadoAnterior(h.getEstadoAnterior())
                                .estadoNuevo(h.getEstadoNuevo())
                                .fechaCambio(h.getFechaCambio())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private String traducirEstado(Pedido.EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case EN_PREPARACION -> "En preparacion";
            case LISTO -> "Listo para entregar";
            case ENTREGADO -> "Entregado";
            case CANCELADO -> "Cancelado";
        };
    }

    private String mensajeEstado(Pedido.EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE -> "Tu pedido ha sido recibido y esta pendiente de revision";
            case EN_PREPARACION -> "Estamos preparando tu pedido con mucho amor";
            case LISTO -> "Tu pedido esta listo y pronto sera entregado";
            case ENTREGADO -> "Tu pedido ha sido entregado. Disfruta!";
            case CANCELADO -> "Este pedido ha sido cancelado";
        };
    }

    private String mensajeEstadoPago(Pago.EstadoPago estado) {
        return switch (estado) {
            case COMPLETADO -> "Pago completado exitosamente.";
            case PENDIENTE -> "Pago pendiente de procesar.";
            case CANCELADO -> "Pago cancelado.";
        };
    }

    private PedidoSeguimientoDTO convertirASeguimientoDTO(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());
        List<HistorialEstadoPedido> historial = historialEstadoPedidoRepository.findByPedidoIdOrderByFechaCambioAsc(pedido.getId());
        List<Notificacion> notificaciones = notificacionRepository.findByPedidoId(pedido.getId());
        Optional<Pago> pago = pagoRepository.findByPedidoId(pedido.getId());

        return PedidoSeguimientoDTO.builder()
                .id(pedido.getId())
                .estado(pedido.getEstado().name())
                .estadoMensaje(mensajeEstado(pedido.getEstado()))
                .fechaCreacion(pedido.getFechaCreacion())
                .total(pedido.getTotal())
                .notasCliente(pedido.getNotasCliente())
                .cantidadItems(detalles.size())
                .estadoPago(pago.map(p -> p.getEstado().name()).orElse("SIN_PAGO"))
                .estadoPagoMensaje(pago.map(p -> mensajeEstadoPago(p.getEstado())).orElse("Pago no registrado aun"))
                .items(detalles.stream().map(d -> {
                    List<PersonalizacionIngrediente> pers =
                            personalizacionIngredienteRepository.findByDetallePedidoId(d.getId());

                    List<String> persTexto = pers.stream()
                            .map(p -> p.getTipo().name() + " " +
                                    (p.getNombreIngrediente() != null ? p.getNombreIngrediente() : "Ingrediente #" + p.getIngredienteId()) +
                                    (p.getCostoExtra().compareTo(java.math.BigDecimal.ZERO) > 0 ? " (+$" + p.getCostoExtra() + ")" : ""))
                            .collect(Collectors.toList());

                    return PedidoSeguimientoDTO.ItemSeguimientoDTO.builder()
                            .nombreProducto(d.getNombreProducto() != null ? d.getNombreProducto() : "Producto #" + d.getProductoId())
                            .cantidad(d.getCantidad())
                            .subtotal(d.getSubtotal())
                            .grupoNombre(d.getGrupoNombre())
                            .personalizaciones(persTexto)
                            .build();
                }).collect(Collectors.toList()))
                .historial(historial.stream()
                        .map(h -> PedidoSeguimientoDTO.HistorialSeguimientoDTO.builder()
                                .mensaje("Cambio de " + (h.getEstadoAnterior() != null ? traducirEstado(Pedido.EstadoPedido.valueOf(h.getEstadoAnterior())) : "nuevo pedido") +
                                        " a " + traducirEstado(Pedido.EstadoPedido.valueOf(h.getEstadoNuevo())))
                                .fecha(h.getFechaCambio())
                                .build())
                        .collect(Collectors.toList()))
                .notificaciones(notificaciones.stream()
                        .map(n -> PedidoSeguimientoDTO.NotificacionSeguimientoDTO.builder()
                                .id(n.getId())
                                .mensaje(n.getMensaje())
                                .fecha(n.getFechaEnvio())
                                .leida(n.getLeido())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}