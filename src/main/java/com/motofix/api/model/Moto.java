package com.motofix.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marca no puede estar vacía")
    private String brand;

    @NotBlank(message = "El modelo no puede estar vacío")
    private String model;

    @NotBlank(message = "La matrícula es obligatoria")
    @Size(min = 7, max = 10, message = "La matrícula debe tener entre 7 y 10 caracteres")
    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Min(value = 1900, message = "El año no puede ser anterior a 1900")
    @Max(value = 2027, message = "El año no puede ser del futuro")
    @Column(name = "`year`") // Mantenemos tu comilla invertida exacta para la palabra reservada
    private Integer modelYear; // Tu atributo original intacto

    @NotBlank(message = "El estado de la moto es obligatorio")
    private String status;
}