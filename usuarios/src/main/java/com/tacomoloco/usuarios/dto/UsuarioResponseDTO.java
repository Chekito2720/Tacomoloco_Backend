package com.tacomoloco.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private String telefono;
    private String direccion;
    private String fotoUrl;
}