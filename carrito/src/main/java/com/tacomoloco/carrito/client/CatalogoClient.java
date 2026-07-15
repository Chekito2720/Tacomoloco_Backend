package com.tacomoloco.carrito.client;

import com.tacomoloco.carrito.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalogo", url = "${catalogo.service.url:http://localhost:8084}")
public interface CatalogoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO getProductoById(@PathVariable Long id);
}