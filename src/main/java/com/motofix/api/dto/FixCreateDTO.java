package com.motofix.api.dto;

import com.motofix.api.model.FixStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FixCreateDTO {

    @NotBlank(message = "La descripción del problema es obligatoria")
    private String description;

    private String technicalNotes;

    @NotNull(message = "El coste total no puede ser nulo")
    private BigDecimal totalCost;

    @NotNull(message = "El ID de la moto es obligatorio")
    private Long motoId;

    private FixStatus status;
}