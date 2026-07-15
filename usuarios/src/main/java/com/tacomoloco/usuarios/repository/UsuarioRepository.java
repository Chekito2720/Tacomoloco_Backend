package com.tacomoloco.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.usuarios.entity.Usuario;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
