package com.tacomoloco.authserver.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioSyncDTO {
    private String nombre;
    private String correo;
    private String password;
    private String rol;
    private Boolean activo;
    private String telefono;
    private String direccion;
    private String fotoUrl;
}
