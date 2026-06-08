package com.motofix.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MechanicCreateDTO {

    @NotBlank(message = "El nombre del mecánico es obligatorio")
    private String name;

    @NotBlank(message = "La especialidad del mecánico es obligatoria")
    private String specialty;

    // Por defecto al contratarlo estará disponible, pero permitimos editarlo mediante el PUT
    private Boolean available = true;
}