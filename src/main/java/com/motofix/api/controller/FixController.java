package com.motofix.api.controller;

import com.motofix.api.model.Fix;
import com.motofix.api.repository.FixRepository;
import com.motofix.api.repository.MotoRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fixes")
public class FixController {

    @Autowired
    private FixRepository fixRepository;

    @Autowired
    private MotoRepository motoRepository;

    // 1. Listar todas las reparaciones (Accesible por Jefe y Mecánico)
    @GetMapping
    public List<Fix> getAllFixes() {
        return fixRepository.findAll();
    }

    // 2. Crear una nueva orden de reparación (Validada y vinculada a una moto)
    @PostMapping
    public Fix createFix(@Valid @RequestBody Fix fix) {
        // Validamos primero que la moto que nos pasan exista en el taller
        if (fix.getMoto() == null || fix.getMoto().getId() == null) {
            throw new IllegalArgumentException("Debe proporcionar un ID de moto válido");
        }

        return motoRepository.findById(fix.getMoto().getId())
                .map(moto -> {
                    fix.setMoto(moto); // Vinculamos la moto real encontrada
                    return fixRepository.save(fix); // Guardamos la reparación
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede crear la reparación: Moto no encontrada con ID: " + fix.getMoto().getId()));
    }

    // 3. Buscar una reparación por su ID
    @GetMapping("/{id}")
    public Fix getFixById(@PathVariable Long id) {
        return fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparación no encontrada con ID: " + id));
    }

    // 4. Editar una reparación (Actualizar notas, coste o estado)
    @PutMapping("/{id}")
    public Fix updateFix(@PathVariable Long id, @Valid @RequestBody Fix fixDetails) {
        return fixRepository.findById(id)
                .map(fix -> {
                    fix.setDescription(fixDetails.getDescription());
                    fix.setTechnicalNotes(fixDetails.getTechnicalNotes());
                    fix.setTotalCost(fixDetails.getTotalCost());
                    fix.setStatus(fixDetails.getStatus()); // Permite cambiar el ENUM (ej: de RECIBIDA a LISTA)

                    // Si el estado pasa a ENTREGADA, guardamos la fecha de resolución automática
                    if (fixDetails.getStatus() == com.motofix.api.model.FixStatus.ENTREGADA) {
                        fix.setResolvedAt(java.time.LocalDateTime.now());
                    }

                    return fixRepository.save(fix);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede actualizar: Reparación no encontrada con ID: " + id));
    }

    // 5. Eliminar una reparación (Solo permitido al Jefe por seguridad)
    @DeleteMapping("/{id}")
    public String deleteFix(@PathVariable Long id) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede eliminar: Reparación no encontrada con ID: " + id));

        fixRepository.delete(fix);
        return "La orden de reparación con ID " + id + " ha sido eliminada correctamente del historial.";
    }
}