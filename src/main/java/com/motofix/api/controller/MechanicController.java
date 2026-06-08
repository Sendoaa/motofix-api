package com.motofix.api.controller;

import com.motofix.api.dto.MechanicCreateDTO;
import com.motofix.api.dto.MechanicDTO;
import com.motofix.api.model.Mechanic;
import com.motofix.api.repository.MechanicRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/mechanics")
public class MechanicController {

    @Autowired
    private MechanicRepository mechanicRepository;

    // 1. Listar todos los mecánicos (Acceso: JEFE y MECÁNICO)
    @GetMapping
    public List<MechanicDTO> getAllMechanics() {
        return mechanicRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Buscar mecánico por ID (Acceso: JEFE y MECÁNICO)
    @GetMapping("/{id}")
    public MechanicDTO getMechanicById(@PathVariable Long id) {
        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mecánico no encontrado con ID: " + id));
        return convertToDTO(mechanic);
    }

    // 3. Contratar / Dar de alta un mecánico (Acceso: SOLO JEFE)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MechanicDTO createMechanic(@Valid @RequestBody MechanicCreateDTO createDTO) {
        Mechanic mechanic = new Mechanic();
        mechanic.setName(createDTO.getName());
        mechanic.setSpecialty(createDTO.getSpecialty());
        mechanic.setAvailable(createDTO.getAvailable());

        Mechanic savedMechanic = mechanicRepository.save(mechanic);
        return convertToDTO(savedMechanic);
    }

    // 4. Editar datos o disponibilidad de un mecánico (Acceso: SOLO JEFE)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MechanicDTO updateMechanic(@PathVariable Long id, @Valid @RequestBody MechanicCreateDTO updateDTO) {
        return mechanicRepository.findById(id)
                .map(mechanic -> {
                    mechanic.setName(updateDTO.getName());
                    mechanic.setSpecialty(updateDTO.getSpecialty());
                    if (updateDTO.getAvailable() != null) {
                        mechanic.setAvailable(updateDTO.getAvailable());
                    }
                    
                    Mechanic updatedMechanic = mechanicRepository.save(mechanic);
                    return convertToDTO(updatedMechanic);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Mecánico no encontrado con ID: " + id));
    }

    // 5. Despedir / Eliminar un mecánico (Acceso: SOLO JEFE)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMechanic(@PathVariable Long id) {
        Mechanic mechanic = mechanicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Mecánico no encontrado con ID: " + id));
        
        mechanicRepository.delete(mechanic);
        return "El mecánico " + mechanic.getName() + " ha sido eliminado correctamente del sistema del taller.";
    }

    private MechanicDTO convertToDTO(Mechanic mechanic) {
        MechanicDTO dto = new MechanicDTO();
        dto.setId(mechanic.getId());
        dto.setName(mechanic.getName());
        dto.setSpecialty(mechanic.getSpecialty());
        dto.setAvailable(mechanic.isAvailable());
        return dto;
    }
}