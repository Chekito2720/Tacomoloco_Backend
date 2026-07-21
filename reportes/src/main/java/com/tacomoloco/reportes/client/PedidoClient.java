package com.tacomoloco.reportes.client;

import com.tacomoloco.reportes.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "pedidos", url = "${pedidos.service.url:http://localhost:8085}")
public interface PedidoClient {

    @GetMapping("/pedidos")
    List<PedidoDTO> getPedidosByFechaBetween(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fin") LocalDateTime fin);

    @GetMapping("/pedidos/{id}")
    PedidoDTO getPedidoById(@PathVariable Long id);
}