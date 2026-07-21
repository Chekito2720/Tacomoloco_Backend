package com.tacomoloco.carrito.client;

import com.tacomoloco.carrito.dto.IngredienteCatalogoDTO;
import com.tacomoloco.carrito.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "catalogo", url = "${catalogo.service.url:http://localhost:8084}")
public interface CatalogoClient {

    @GetMapping("/catalogo/{id}")
    ProductoDTO getProductoById(@PathVariable Long id);

    @GetMapping("/catalogo/{id}/ingredientes")
    List<IngredienteCatalogoDTO> getIngredientesByProductoId(@PathVariable Long id);
}