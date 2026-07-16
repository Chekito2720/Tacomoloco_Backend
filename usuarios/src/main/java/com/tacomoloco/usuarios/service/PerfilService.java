package com.tacomoloco.usuarios.service;

import com.tacomoloco.usuarios.dto.PerfilRequestDTO;
import com.tacomoloco.usuarios.dto.PerfilResponseDTO;
import com.tacomoloco.usuarios.entity.Perfil;
import com.tacomoloco.usuarios.entity.Usuario;
import com.tacomoloco.usuarios.repository.PerfilRepository;
import com.tacomoloco.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public PerfilResponseDTO obtenerPorUsuarioId(Long usuarioId) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado para usuario: " + usuarioId));
        return convertirADTO(perfil);
    }

    @Transactional
    public PerfilResponseDTO actualizar(Long usuarioId, PerfilRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Perfil nuevo = new Perfil();
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        if (request.getTelefono() != null) {
            perfil.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            perfil.setDireccion(request.getDireccion());
        }
        if (request.getFotoUrl() != null) {
            perfil.setFotoUrl(request.getFotoUrl());
        }

        perfil = perfilRepository.save(perfil);
        return convertirADTO(perfil);
    }

    private PerfilResponseDTO convertirADTO(Perfil perfil) {
        return PerfilResponseDTO.builder()
                .id(perfil.getId())
                .usuarioId(perfil.getUsuario().getId())
                .telefono(perfil.getTelefono())
                .direccion(perfil.getDireccion())
                .fotoUrl(perfil.getFotoUrl())
                .build();
    }
}