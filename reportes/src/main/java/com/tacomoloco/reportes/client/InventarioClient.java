package com.tacomoloco.reportes.client;

import com.tacomoloco.reportes.dto.IngredienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "inventario", url = "${inventario.service.url:http://localhost:8082}")
public interface InventarioClient {

    @GetMapping("/inventario")
    List<IngredienteDTO> getAllIngredientes();

    @GetMapping("/inventario/{id}")
    IngredienteDTO getIngredienteById(@PathVariable Long id);
}