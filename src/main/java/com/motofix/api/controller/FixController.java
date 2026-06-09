package com.motofix.api.controller;

import com.motofix.api.dto.FixCreateDTO;
import com.motofix.api.dto.FixDTO;
import com.motofix.api.model.Fix;
import com.motofix.api.model.FixStatus;
import com.motofix.api.repository.FixRepository;
import com.motofix.api.repository.MotoRepository;
import com.motofix.api.repository.MechanicRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fixes")
public class FixController {

    @Autowired
    private FixRepository fixRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    // 1. Listar todas las reparaciones
    @GetMapping
    public List<FixDTO> getAllFixes() {
        return fixRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Buscar reparación por ID
    @GetMapping("/{id}")
    public FixDTO getFixById(@PathVariable Long id) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparación no encontrada con ID: " + id));
        return convertToDTO(fix);
    }

    // 3. Registrar una nueva reparación (Vincula Moto y Mecánico)
    @PostMapping
    public FixDTO createFix(@Valid @RequestBody FixCreateDTO createDTO) {
        // Buscamos la moto en la BD
        var moto = motoRepository.findById(createDTO.getMotoId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la reparación: Moto no encontrada con ID: " + createDTO.getMotoId()));

        // Buscamos al mecánico asignado en la BD
        var mechanic = mechanicRepository.findById(createDTO.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la reparación: Mecánico no encontrado con ID: " + createDTO.getMechanicId()));

        Fix fix = new Fix();
        fix.setDescription(createDTO.getDescription());
        fix.setTechnicalNotes(createDTO.getTechnicalNotes());
        fix.setTotalCost(createDTO.getTotalCost());
        fix.setMoto(moto);
        fix.setMechanic(mechanic);

        if (createDTO.getStatus() != null) {
            fix.setStatus(createDTO.getStatus());
        }

        Fix savedFix = fixRepository.save(fix);
        return convertToDTO(savedFix);
    }

    // 4. Editar una reparación
    @PutMapping("/{id}")
    public FixDTO updateFix(@PathVariable Long id, @Valid @RequestBody FixCreateDTO updateDTO) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Reparación no encontrada con ID: " + id));

        // Actualizamos datos básicos
        fix.setDescription(updateDTO.getDescription());
        fix.setTechnicalNotes(updateDTO.getTechnicalNotes());
        fix.setTotalCost(updateDTO.getTotalCost());

        // Manejo del estado y fecha de resolución automática
        if (updateDTO.getStatus() != null) {
            if (updateDTO.getStatus() == FixStatus.ENTREGADA && fix.getStatus() != FixStatus.ENTREGADA) {
                fix.setResolvedAt(LocalDateTime.now());
            } else if (updateDTO.getStatus() != FixStatus.ENTREGADA) {
                fix.setResolvedAt(null);
            }
            fix.setStatus(updateDTO.getStatus());
        }

        // Si cambia de moto, la buscamos y actualizamos
        if (!fix.getMoto().getId().equals(updateDTO.getMotoId())) {
            var newMoto = motoRepository.findById(updateDTO.getMotoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Moto no encontrada con ID: " + updateDTO.getMotoId()));
            fix.setMoto(newMoto);
        }

        // Si cambia de mecánico asignado, lo buscamos y actualizamos
        if (fix.getMechanic() == null || !fix.getMechanic().getId().equals(updateDTO.getMechanicId())) {
            var newMechanic = mechanicRepository.findById(updateDTO.getMechanicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mecánico no encontrado con ID: " + updateDTO.getMechanicId()));
            fix.setMechanic(newMechanic);
        }

        Fix updatedFix = fixRepository.save(fix);
        return convertToDTO(updatedFix);
    }

    // 5. Eliminar una reparación
    @DeleteMapping("/{id}")
    public String deleteFix(@PathVariable Long id) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Reparación no encontrada con ID: " + id));
        
        fixRepository.delete(fix);
        return "La orden de reparación con ID " + id + " ha sido eliminada del sistema.";
    }

    private FixDTO convertToDTO(Fix fix) {
        FixDTO dto = new FixDTO();
        dto.setId(fix.getId());
        dto.setDescription(fix.getDescription());
        dto.setTechnicalNotes(fix.getTechnicalNotes());
        dto.setTotalCost(fix.getTotalCost());
        dto.setStatus(fix.getStatus());
        dto.setCreatedAt(fix.getCreatedAt());
        dto.setResolvedAt(fix.getResolvedAt());

        if (fix.getMoto() != null) {
            dto.setMotoId(fix.getMoto().getId());
            dto.setMotoBrand(fix.getMoto().getBrand());
            dto.setMotoModel(fix.getMoto().getModel());
            dto.setLicensePlate(fix.getMoto().getLicensePlate());
        }

        if (fix.getMechanic() != null) {
            dto.setMechanicId(fix.getMechanic().getId());
            dto.setMechanicName(fix.getMechanic().getName());
        }

        return dto;
    }
}