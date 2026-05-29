package com.motofix.api.controller;

import com.motofix.api.dto.FixCreateDTO;
import com.motofix.api.dto.FixDTO;
import com.motofix.api.model.Fix;
import com.motofix.api.model.FixStatus;
import com.motofix.api.repository.FixRepository;
import com.motofix.api.repository.MotoRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fixes")
public class FixController {

    @Autowired
    private FixRepository fixRepository;

    @Autowired
    private MotoRepository motoRepository;

    // 1. Listar todas las reparaciones (Devuelve lista de DTOs)
    @GetMapping
    public List<FixDTO> getAllFixes() {
        return fixRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Buscar una reparación por su ID (Devuelve DTO)
    @GetMapping("/{id}")
    public FixDTO getFixById(@PathVariable Long id) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparación no encontrada con ID: " + id));
        return convertToDTO(fix);
    }

    // 3. Crear una nueva orden de reparación utilizando FixCreateDTO
    @PostMapping
    public FixDTO createFix(@Valid @RequestBody FixCreateDTO createDTO) {
        return motoRepository.findById(createDTO.getMotoId())
                .map(moto -> {
                    Fix fix = new Fix();
                    fix.setDescription(createDTO.getDescription());
                    fix.setTechnicalNotes(createDTO.getTechnicalNotes());
                    fix.setTotalCost(createDTO.getTotalCost());
                    fix.setMoto(moto); // Vinculamos la moto real
                    
                    Fix savedFix = fixRepository.save(fix);
                    return convertToDTO(savedFix);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la reparación: Moto no encontrada con ID: " + createDTO.getMotoId()));
    }

    // 4. Editar una reparación utilizando FixCreateDTO para actualizar datos y estado
    @PutMapping("/{id}")
    public FixDTO updateFix(@PathVariable Long id, @Valid @RequestBody FixCreateDTO updateDTO) {
        return fixRepository.findById(id)
                .map(fix -> {
                    fix.setDescription(updateDTO.getDescription());
                    fix.setTechnicalNotes(updateDTO.getTechnicalNotes());
                    fix.setTotalCost(updateDTO.getTotalCost());
                    
                    // Si nos envían un estado en el JSON, lo actualizamos
                    if (updateDTO.getStatus() != null) {
                        fix.setStatus(updateDTO.getStatus());
                        
                        // Si el estado cambia a ENTREGADA, clavamos la fecha de resolución
                        if (updateDTO.getStatus() == FixStatus.ENTREGADA) {
                            fix.setResolvedAt(java.time.LocalDateTime.now());
                        }
                    }

                    Fix updatedFix = fixRepository.save(fix);
                    return convertToDTO(updatedFix);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Reparación no encontrada con ID: " + id));
    }

    // 5. Eliminar una reparación
    @DeleteMapping("/{id}")
    public String deleteFix(@PathVariable Long id) {
        Fix fix = fixRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Reparación no encontrada con ID: " + id));
        
        fixRepository.delete(fix);
        return "La orden de reparación con ID " + id + " ha sido eliminada correctamente del historial.";
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE CONVERSIÓN (MAPPERS)
    // ==========================================
    private FixDTO convertToDTO(Fix fix) {
        FixDTO dto = new FixDTO();
        dto.setId(fix.getId());
        dto.setDescription(fix.getDescription());
        dto.setTechnicalNotes(fix.getTechnicalNotes());
        dto.setTotalCost(fix.getTotalCost());
        dto.setStatus(fix.getStatus());
        dto.setCreatedAt(fix.getCreatedAt());
        dto.setResolvedAt(fix.getResolvedAt());
        
        // Mapeamos los datos sueltos de la moto evitando traer el objeto Client entero
        if (fix.getMoto() != null) {
            dto.setMotoId(fix.getMoto().getId());
            dto.setMotoBrand(fix.getMoto().getBrand());
            dto.setMotoModel(fix.getMoto().getModel());
            dto.setLicensePlate(fix.getMoto().getLicensePlate());
        }
        
        return dto;
    }
}