package com.tacomoloco.catalogo.client;

import com.tacomoloco.catalogo.dto.IngredienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventario", url = "${inventario.service.url:http://localhost:8082}")
public interface IngredienteClient {

    @GetMapping("/api/ingredientes/{id}")
    IngredienteDTO getIngredienteById(@PathVariable Long id);
}