package com.tacomoloco.usuarios.controller;

import com.tacomoloco.usuarios.dto.*;
import com.tacomoloco.usuarios.service.PerfilService;
import com.tacomoloco.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PerfilService perfilService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(
            @RequestParam(value = "rol", required = false) String rol,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @RequestParam(value = "nombre", required = false) String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
        }
        if (rol != null && !rol.isBlank()) {
            return ResponseEntity.ok(usuarioService.listarPorRol(rol));
        }
        if (activo != null) {
            return ResponseEntity.ok(activo ? usuarioService.listarActivos() : usuarioService.listarInactivos());
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(usuarioService.obtenerPorCorreo(correo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequestDTO request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/deshabilitar")
    public ResponseEntity<UsuarioResponseDTO> deshabilitar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.deshabilitar(id));
    }

    @PatchMapping("/{id}/habilitar")
    public ResponseEntity<UsuarioResponseDTO> habilitar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.habilitar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PerfilResponseDTO> obtenerPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(perfilService.obtenerPorUsuarioId(id));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<PerfilResponseDTO> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody PerfilRequestDTO request) {
        return ResponseEntity.ok(perfilService.actualizar(id, request));
    }
}