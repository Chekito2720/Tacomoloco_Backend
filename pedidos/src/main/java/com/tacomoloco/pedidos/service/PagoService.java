package com.tacomoloco.pedidos.service;

import com.tacomoloco.pedidos.dto.CheckoutRequestDTO;
import com.tacomoloco.pedidos.dto.CheckoutResponseDTO;
import com.tacomoloco.pedidos.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
                pedidoService.agregarDetalle(detalle);
            }
        }

        Pago pago = pedidoService.simularPago(pedido, "TACOMOLOCO_SIMULADO");

        pedidoService.crearNotificacion(pedido,
                "Tu pedido #" + pedido.getId() + " ha sido recibido y esta en preparacion. Total: $" + pedido.getTotal());

        return CheckoutResponseDTO.builder()
                .pedidoId(pedido.getId())
                .estado(pedido.getEstado().name())
                .total(pedido.getTotal())
                .fechaCreacion(pedido.getFechaCreacion())
                .estadoPago(pago.getEstado().name())
                .build();
    }
}