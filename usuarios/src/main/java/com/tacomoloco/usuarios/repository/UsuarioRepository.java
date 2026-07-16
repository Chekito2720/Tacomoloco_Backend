package com.tacomoloco.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.usuarios.entity.Usuario;
import com.tacomoloco.usuarios.entity.Usuario.RolUsuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByActivoFalse();

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}