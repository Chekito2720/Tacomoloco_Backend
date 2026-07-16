package com.tacomoloco.usuarios.dto;

import lombok.Data;

@Data
public class PerfilRequestDTO {
    private String telefono;
    private String direccion;
    private String fotoUrl;
}