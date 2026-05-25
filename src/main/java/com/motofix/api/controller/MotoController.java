package com.motofix.api.controller;

import com.motofix.api.model.Moto;
import com.motofix.api.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    // 1. Obtener todas las motos del taller
    @GetMapping
    public List<Moto> getAllMotos() {
        return motoRepository.findAll();
    }

    // 2. Registrar una nueva moto en el taller
    @PostMapping
    public Moto createMoto(@RequestBody Moto moto) {
        return motoRepository.save(moto);
    }

    // 3. Buscar una moto por su ID
    @GetMapping("/{id}")
    public Moto getMotoById(@PathVariable Long id) {
        return motoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moto not found with id: " + id));
    }

    // 4. Actualizar los datos de una moto existente
    @PutMapping("/{id}")
    public Moto updateMoto(@PathVariable Long id, @RequestBody Moto motoDetails) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moto not found with id: " + id));

        moto.setBrand(motoDetails.getBrand());
        moto.setModel(motoDetails.getModel());
        moto.setLicensePlate(motoDetails.getLicensePlate());
        moto.setModelYear(motoDetails.getModelYear());
        moto.setStatus(motoDetails.getStatus());

        return motoRepository.save(moto);
    }

    // 5. Borrar una moto del taller
    @DeleteMapping("/{id}")
    public String deleteMoto(@PathVariable Long id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moto not found with id: " + id));
        
        motoRepository.delete(moto);
        return "Moto with id " + id + " has been successfully deleted.";
    }
}