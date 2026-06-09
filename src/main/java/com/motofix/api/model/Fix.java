package com.motofix.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción del problema es obligatoria")
    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String technicalNotes;

    @NotNull(message = "El coste total no puede ser nulo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixStatus status = FixStatus.RECIBIDA;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    // RELACIÓN: Muchas Reparaciones pertenecen a una Moto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moto_id", nullable = false)
    @NotNull(message = "La reparación debe estar vinculada a una moto obligatoriamente")
    private Moto moto;

    // NUEVA RELACIÓN: Muchas Reparaciones pertenecen a un Mecánico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FixStatus.RECIBIDA;
        }
    }
}