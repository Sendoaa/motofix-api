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
}
