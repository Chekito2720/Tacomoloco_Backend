package com.tacomoloco.usuarios.controller;

import com.tacomoloco.usuarios.dto.PerfilRequestDTO;
import com.tacomoloco.usuarios.dto.PerfilResponseDTO;
import com.tacomoloco.usuarios.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfiles")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PerfilResponseDTO> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(perfilService.obtenerPorUsuarioId(usuarioId));
    }

    @PutMapping("/usuario/{usuarioId}")
    public ResponseEntity<PerfilResponseDTO> actualizar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody PerfilRequestDTO request) {
        return ResponseEntity.ok(perfilService.actualizar(usuarioId, request));
    }
}