package com.tacomoloco.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo no valido")
    private String correo;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private Boolean activo;
    private String telefono;
    private String direccion;
    private String fotoUrl;
}