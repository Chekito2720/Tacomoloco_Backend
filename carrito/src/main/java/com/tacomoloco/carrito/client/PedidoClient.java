package com.tacomoloco.carrito.client;

import com.tacomoloco.carrito.dto.PedidoCheckoutRequestDTO;
import com.tacomoloco.carrito.dto.PedidoCheckoutResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pedidos", url = "${pedidos.service.url:http://localhost:8085}")
public interface PedidoClient {

    @PostMapping("/pedidos/checkout")
    PedidoCheckoutResponseDTO checkout(@RequestBody PedidoCheckoutRequestDTO request);
}