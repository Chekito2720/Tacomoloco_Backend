package com.tacomoloco.usuarios.service;

import com.tacomoloco.usuarios.dto.*;
import com.tacomoloco.usuarios.entity.Perfil;
import com.tacomoloco.usuarios.entity.Usuario;
import com.tacomoloco.usuarios.entity.Usuario.RolUsuario;
import com.tacomoloco.usuarios.repository.PerfilRepository;
import com.tacomoloco.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya esta registrado: " + request.getCorreo());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(RolUsuario.valueOf(request.getRol().toUpperCase()));
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : true);
        usuario = usuarioRepository.save(usuario);

        Perfil perfil = new Perfil();
        perfil.setUsuario(usuario);
        perfil.setTelefono(request.getTelefono());
        perfil.setDireccion(request.getDireccion());
        perfil.setFotoUrl(request.getFotoUrl());
        perfilRepository.save(perfil);

        return convertirAResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return convertirAResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con correo: " + correo));
        return convertirAResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        RolUsuario rolEnum = RolUsuario.valueOf(rol.toUpperCase());
        return usuarioRepository.findByRol(rolEnum).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarInactivos() {
        return usuarioRepository.findByActivoFalse().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getRol() != null) {
            usuario.setRol(RolUsuario.valueOf(request.getRol().toUpperCase()));
        }
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
        usuarioRepository.save(usuario);

        Perfil perfil = perfilRepository.findByUsuarioId(id).orElse(null);
        if (perfil != null) {
            if (request.getTelefono() != null) {
                perfil.setTelefono(request.getTelefono());
            }
            if (request.getDireccion() != null) {
                perfil.setDireccion(request.getDireccion());
            }
            if (request.getFotoUrl() != null) {
                perfil.setFotoUrl(request.getFotoUrl());
            }
            perfilRepository.save(perfil);
        }

        return convertirAResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO deshabilitar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO habilitar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        perfilRepository.findByUsuarioId(id).ifPresent(perfilRepository::delete);
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuario.getId()).orElse(null);

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .activo(usuario.getActivo())
                .fechaRegistro(usuario.getFechaRegistro())
                .telefono(perfil != null ? perfil.getTelefono() : null)
                .direccion(perfil != null ? perfil.getDireccion() : null)
                .fotoUrl(perfil != null ? perfil.getFotoUrl() : null)
                .build();
    }
}