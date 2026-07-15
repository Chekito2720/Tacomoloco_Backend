package com.tacomoloco.inventario.controller;

import com.tacomoloco.inventario.entity.Ingrediente;
import com.tacomoloco.inventario.service.IngredienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @PostMapping
    public ResponseEntity<Ingrediente> crear(@RequestBody Ingrediente ingrediente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteService.crear(ingrediente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingrediente> obtenerPorId(@PathVariable Long id) {
        return ingredienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ingrediente>> listarTodos() {
        return ResponseEntity.ok(ingredienteService.listarTodos());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Ingrediente>> listarPorEstado(@PathVariable String estado) {
        Ingrediente.EstadoIngrediente estadoEnum = Ingrediente.EstadoIngrediente.valueOf(estado.toUpperCase());
        return ResponseEntity.ok(ingredienteService.listarPorEstado(estadoEnum));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingrediente> actualizar(@PathVariable Long id, @RequestBody Ingrediente ingrediente) {
        return ResponseEntity.ok(ingredienteService.actualizar(id, ingrediente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/consumo")
    public ResponseEntity<Ingrediente> registrarConsumo(@PathVariable Long id, @RequestParam Double cantidad) {
        return ResponseEntity.ok(ingredienteService.registrarConsumo(id, cantidad));
    }

    @PostMapping("/{id}/reabastecer")
    public ResponseEntity<Ingrediente> reabastecer(@PathVariable Long id, @RequestParam Double cantidad) {
        return ResponseEntity.ok(ingredienteService.reabastecer(id, cantidad));
    }
}