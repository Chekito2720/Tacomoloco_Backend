package com.tacomoloco.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AgregarItemRequestDTO {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private Long grupoPedidoId;

    private List<PersonalizacionRequestDTO> personalizaciones;

    @Data
    public static class PersonalizacionRequestDTO {
        @NotNull(message = "El ID del ingrediente es obligatorio")
        private Long ingredienteId;

        @NotNull(message = "El tipo de personalizacion es obligatorio")
        private String tipo;
    }
}