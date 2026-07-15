package com.tacomoloco.carrito.controller;

import com.tacomoloco.carrito.dto.AgregarItemRequestDTO;
import com.tacomoloco.carrito.dto.CarritoResponseDTO;
import com.tacomoloco.carrito.dto.PedidoCheckoutResponseDTO;
import com.tacomoloco.carrito.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(@RequestHeader("X-Cliente-Id") Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerOCrear(clienteId));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(
            @RequestHeader("X-Cliente-Id") Long clienteId,
            @Valid @RequestBody AgregarItemRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.agregarItem(clienteId, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidadItem(
            @RequestHeader("X-Cliente-Id") Long clienteId,
            @PathVariable Long itemId,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidadItem(clienteId, itemId, cantidad));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            @RequestHeader("X-Cliente-Id") Long clienteId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(clienteId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@RequestHeader("X-Cliente-Id") Long clienteId) {
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<PedidoCheckoutResponseDTO> checkout(
            @RequestHeader("X-Cliente-Id") Long clienteId,
            @RequestParam(required = false) String notas) {
        return ResponseEntity.ok(carritoService.checkout(clienteId, notas));
    }
}