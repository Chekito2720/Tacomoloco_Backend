package com.tacomoloco.authserver.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class UsuariosSyncClient {

    private final RestClient restClient;

    public UsuariosSyncClient(@Value("${usuarios.service.url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void crearUsuario(UsuarioSyncDTO dto, String bearerToken) {
        try {
            restClient.post()
                .uri("/usuarios")
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
            log.info("Usuario sincronizado con microservicio usuarios: {}", dto.getCorreo());
        } catch (Exception e) {
            log.warn("No se pudo sincronizar el usuario con microservicio usuarios (correo={}): {}",
                dto.getCorreo(), e.getMessage());
        }
    }
}
