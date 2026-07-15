package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.dto.CheckoutRequestDTO;
import com.tacomoloco.pedidos.dto.CheckoutResponseDTO;
import com.tacomoloco.pedidos.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PedidoService pedidoService;

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

        Pago pago = pedidoService.simularPago(pedido, "TACOMOLOCO_SIMULADO");

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
}