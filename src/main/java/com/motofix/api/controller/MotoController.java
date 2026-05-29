package com.motofix.api.controller;

import com.motofix.api.dto.MotoCreateDTO;
import com.motofix.api.dto.MotoDTO;
import com.motofix.api.model.Moto;
import com.motofix.api.repository.ClientRepository;
import com.motofix.api.repository.MotoRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private ClientRepository clientRepository;

    // 1. Listar todas las motos
    @GetMapping
    public List<MotoDTO> getAllMotos() {
        return motoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Buscar moto por ID
    @GetMapping("/{id}")
    public MotoDTO getMotoById(@PathVariable Long id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moto no encontrada con ID: " + id));
        return convertToDTO(moto);
    }

    // 3. Registrar una nueva moto vinculándola por clientId
    @PostMapping
    public MotoDTO createMoto(@Valid @RequestBody MotoCreateDTO createDTO) {
        return clientRepository.findById(createDTO.getClientId())
                .map(client -> {
                    Moto moto = new Moto();
                    moto.setBrand(createDTO.getBrand());
                    moto.setModel(createDTO.getModel());
                    moto.setLicensePlate(createDTO.getLicensePlate());
                    moto.setModelYear(createDTO.getModelYear());
                    moto.setStatus(createDTO.getStatus());
                    moto.setClient(client);
                    
                    Moto savedMoto = motoRepository.save(moto);
                    return convertToDTO(savedMoto);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede registrar la moto: Cliente no encontrado con ID: " + createDTO.getClientId()));
    }

    // 4. Editar los datos de una moto
    @PutMapping("/{id}")
    public MotoDTO updateMoto(@PathVariable Long id, @Valid @RequestBody MotoCreateDTO updateDTO) {
        return motoRepository.findById(id)
                .map(moto -> {
                    moto.setBrand(updateDTO.getBrand());
                    moto.setModel(updateDTO.getModel());
                    moto.setLicensePlate(updateDTO.getLicensePlate());
                    moto.setModelYear(updateDTO.getModelYear());
                    moto.setStatus(updateDTO.getStatus());
                    
                    // Si deciden reasignar la moto a otro cliente, lo buscamos y actualizamos
                    if (!moto.getClient().getId().equals(updateDTO.getClientId())) {
                        var newClient = clientRepository.findById(updateDTO.getClientId())
                                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + updateDTO.getClientId()));
                        moto.setClient(newClient);
                    }

                    Moto updatedMoto = motoRepository.save(moto);
                    return convertToDTO(updatedMoto);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Moto no encontrada con ID: " + id));
    }

    // 5. Eliminar una moto del sistema
    @DeleteMapping("/{id}")
    public String deleteMoto(@PathVariable Long id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Moto no encontrada con ID: " + id));
        
        motoRepository.delete(moto);
        return "La moto " + moto.getBrand() + " " + moto.getModel() + " con matrícula " + moto.getLicensePlate() + " ha sido eliminada correctamente.";
    }

    private MotoDTO convertToDTO(Moto moto) {
        MotoDTO dto = new MotoDTO();
        dto.setId(moto.getId());
        dto.setBrand(moto.getBrand());
        dto.setModel(moto.getModel());
        dto.setLicensePlate(moto.getLicensePlate());
        dto.setModelYear(moto.getModelYear());
        dto.setStatus(moto.getStatus());
        
        if (moto.getClient() != null) {
            dto.setClientId(moto.getClient().getId());
            dto.setClientName(moto.getClient().getName());
        }
        
        return dto;
    }
}