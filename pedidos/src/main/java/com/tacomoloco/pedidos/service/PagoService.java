package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.dto.CheckoutRequestDTO;
import com.tacomoloco.pedidos.dto.CheckoutResponseDTO;
import com.tacomoloco.pedidos.dto.PagoResponseDTO;
import com.tacomoloco.pedidos.entity.*;
import com.tacomoloco.pedidos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PedidoService pedidoService;
    private final PagoRepository pagoRepository;

    @Transactional
    public CheckoutResponseDTO procesarCheckout(CheckoutRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(request.getClienteId());
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        pedido.setNotasCliente(request.getNotasCliente());
        pedido = pedidoService.crearPedido(pedido);

        if (request.getDetalles() != null) {
            for (CheckoutRequestDTO.DetalleCheckoutDTO detalleReq : request.getDetalles()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProductoId(detalleReq.getProductoId());
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                detalle.setSubtotal(detalleReq.getSubtotal());
                detalle.setNombreProducto(detalleReq.getNombreProducto());
                detalle.setGrupoNombre(detalleReq.getGrupoNombre());
                detalle.setGrupoNota(detalleReq.getGrupoNota());
                pedidoService.agregarDetalle(detalle);

                if (detalleReq.getPersonalizaciones() != null) {
                    for (CheckoutRequestDTO.PersonalizacionCheckoutDTO persReq : detalleReq.getPersonalizaciones()) {
                        PersonalizacionIngrediente pers = new PersonalizacionIngrediente();
                        pers.setDetallePedido(detalle);
                        pers.setIngredienteId(persReq.getIngredienteId());
                        pers.setTipo(PersonalizacionIngrediente.TipoPersonalizacion.valueOf(persReq.getTipo()));
                        pers.setCostoExtra(persReq.getCostoExtra() != null ? persReq.getCostoExtra() : BigDecimal.ZERO);
                        pers.setNombreIngrediente(persReq.getNombreIngrediente());
                        pedidoService.agregarPersonalizacion(pers);
                    }
                }
            }
        }

        Pago pago = simularPagoExitoso(pedido, "TACOMOLOCO_SIMULADO");

        String nombresGrupos = request.getDetalles() != null ? request.getDetalles().stream()
                .filter(d -> d.getGrupoNombre() != null && !d.getGrupoNombre().isBlank())
                .map(CheckoutRequestDTO.DetalleCheckoutDTO::getGrupoNombre)
                .distinct()
                .collect(Collectors.joining(", ")) : "";

        String mensaje = "Tu pedido #" + pedido.getId() + " ha sido recibido y esta en preparacion. Total: $" + pedido.getTotal();
        if (!nombresGrupos.isEmpty()) {
            mensaje += " | Grupos: " + nombresGrupos;
        }
        pedidoService.crearNotificacion(pedido, mensaje);

        return CheckoutResponseDTO.builder()
                .pedidoId(pedido.getId())
                .estado(pedido.getEstado().name())
                .total(pedido.getTotal())
                .fechaCreacion(pedido.getFechaCreacion())
                .estadoPago(pago.getEstado().name())
                .build();
    }

    @Transactional
    public PagoResponseDTO simularPagoPedido(Long pedidoId, String metodo) {
        Pedido pedido = pedidoService.obtenerPorId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        Optional<Pago> existente = pagoRepository.findByPedidoId(pedidoId);
        if (existente.isPresent() && existente.get().getEstado() == Pago.EstadoPago.SIMULADO_EXITOSO) {
            return convertirADTO(existente.get());
        }

        Pago pago = simularPagoExitoso(pedido, metodo != null ? metodo : "TACOMOLOCO_SIMULADO");

        pedidoService.crearNotificacion(pedido,
                "Pago simulado exitosamente para tu pedido #" + pedido.getId() + ". Total: $" + pago.getMonto());

        return convertirADTO(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPagoPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("No hay pago registrado para el pedido: " + pedidoId));
        return convertirADTO(pago);
    }

    private Pago simularPagoExitoso(Pedido pedido, String metodo) {
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodo(metodo);
        pago.setMonto(pedido.getTotal());
        pago.setEstado(Pago.EstadoPago.SIMULADO_EXITOSO);
        return pagoRepository.save(pago);
    }

    private PagoResponseDTO convertirADTO(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedido().getId())
                .metodo(pago.getMetodo())
                .monto(pago.getMonto())
                .estado(pago.getEstado().name())
                .estadoMensaje(mensajeEstadoPago(pago.getEstado()))
                .fecha(pago.getFecha())
                .build();
    }

    private String mensajeEstadoPago(Pago.EstadoPago estado) {
        return switch (estado) {
            case SIMULADO_EXITOSO -> "Pago simulado exitosamente. En el futuro se conectara con un sistema de pago real.";
            case SIMULADO_FALLIDO -> "El pago simulado ha fallado. Intentalo de nuevo.";
        };
    }
}