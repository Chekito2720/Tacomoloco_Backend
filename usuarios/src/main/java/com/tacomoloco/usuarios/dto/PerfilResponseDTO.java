package com.tacomoloco.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilResponseDTO {
    private Long id;
    private Long usuarioId;
    private String telefono;
    private String direccion;
    private String fotoUrl;
}