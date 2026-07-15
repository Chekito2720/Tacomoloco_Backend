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
}