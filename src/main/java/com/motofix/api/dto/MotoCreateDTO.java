package com.motofix.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MotoCreateDTO {

    @NotBlank(message = "La marca es obligatoria")
    private String brand;

    @NotBlank(message = "El modelo es obligatorio")
    private String model;

    @NotBlank(message = "La matrícula es obligatoria")
    @Size(min = 4, max = 15, message = "La matrícula debe tener un tamaño válido")
    private String licensePlate;

    @NotNull(message = "El año del modelo no puede ser nulo")
    private Integer modelYear;

    @NotBlank(message = "El estado inicial de la moto es obligatorio")
    private String status;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;
}