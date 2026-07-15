package com.tacomoloco.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.usuarios.entity.Perfil;

import java.util.Optional;


public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByUsuarioId(Long usuarioId);
}
