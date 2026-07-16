package com.tacomoloco.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioUpdateRequestDTO {
    @Size(max = 255)
    private String nombre;

    private String rol;
    private Boolean activo;
    private String telefono;
    private String direccion;
    private String fotoUrl;
}