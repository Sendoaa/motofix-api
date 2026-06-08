package com.motofix.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mechanics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty; // Ej: "Electricidad", "Motores 2T/4T", "Suspensiones"

    @Column(name = "is_available")
    private boolean available = true; // Para saber si le podemos asignar motos o está de baja/vacaciones
}