package com.motofix.api.controller;

import jakarta.validation.Valid;
import com.motofix.api.exception.DuplicateResourceException;
import com.motofix.api.exception.ResourceNotFoundException;
import com.motofix.api.model.Client;
import com.motofix.api.model.Moto;
import com.motofix.api.repository.ClientRepository;
import com.motofix.api.repository.MotoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

    private final MotoRepository motoRepository;
    private final ClientRepository clientRepository;

    // Inyección por constructor
    public MotoController(MotoRepository motoRepository, ClientRepository clientRepository) {
        this.motoRepository = motoRepository;
        this.clientRepository = clientRepository;
    }

    // 1. Obtener todas las motos del taller
    @GetMapping
    public List<Moto> getAllMotos() {
        return motoRepository.findAll();
    }

    // 2. Registrar una nueva moto en el taller
    @PostMapping
    public Moto createMoto(@Valid @RequestBody Moto moto) {
        // Validación A: Que nos pasen un cliente con su ID en el JSON
        if (moto.getClient() == null || moto.getClient().getId() == null) {
            throw new DuplicateResourceException("Para registrar una moto debes asignar el ID de un cliente válido.");
        }

        // Validación B: Que ese ID de cliente exista realmente en PostgreSQL
        Long clientId = moto.getClient().getId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede registrar la moto: El cliente con ID " + clientId + " no existe."));

        // Validación C: Que la matrícula no esté duplicada
        if (motoRepository.findByLicensePlate(moto.getLicensePlate()).isPresent()) {
            throw new DuplicateResourceException("La matrícula '" + moto.getLicensePlate() + "' ya está registrada en el taller.");
        }
        
        moto.setClient(client);
        return motoRepository.save(moto);
    }

    // 3. Buscar una moto por su ID
    @GetMapping("/{id}")
    public Moto getMotoById(@PathVariable Long id) {
        return motoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moto no encontrada con el ID: " + id));
    }

    // 4. Actualizar los datos de una moto existente
    @PutMapping("/{id}")
    public Moto updateMoto(@PathVariable Long id, @Valid @RequestBody Moto motoDetails) {
        // 1. Comprobar si la moto que se quiere actualizar existe de verdad
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Moto no encontrada con el ID: " + id));

        // 2. Validar que nos pasan un cliente válido en la petición de actualización
        if (motoDetails.getClient() == null || motoDetails.getClient().getId() == null) {
            throw new DuplicateResourceException("Para actualizar la moto debes mantener o asignar el ID de un cliente válido.");
        }

        // 3. Validar si ese cliente existe realmente en PostgreSQL
        Long clientId = motoDetails.getClient().getId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar la moto: El cliente con ID " + clientId + " no existe."));

        // 4. Comprobar que la matrícula no choque con otra moto
        if (!moto.getLicensePlate().equals(motoDetails.getLicensePlate()) &&
            motoRepository.findByLicensePlate(motoDetails.getLicensePlate()).isPresent()) {
            throw new DuplicateResourceException("La nueva matrícula '" + motoDetails.getLicensePlate() + "' ya pertenece a otra moto.");
        }

        // 5. Actualizamos los datos básicos y vinculamos el cliente verificado
        moto.setBrand(motoDetails.getBrand());
        moto.setModel(motoDetails.getModel());
        moto.setLicensePlate(motoDetails.getLicensePlate());
        moto.setModelYear(motoDetails.getModelYear());
        moto.setStatus(motoDetails.getStatus());
        moto.setClient(client);

        return motoRepository.save(moto);
    }

    // 5. Borrar una moto del taller
    @DeleteMapping("/{id}")
    public String deleteMoto(@PathVariable Long id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Moto no encontrada con el ID: " + id));

        motoRepository.delete(moto);
        return "Moto con ID " + id + " eliminada correctamente del taller.";
    }
}