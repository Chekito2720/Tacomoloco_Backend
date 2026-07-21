package com.tacomoloco.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tacomoloco.authserver.entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
