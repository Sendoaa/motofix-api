package com.motofix.api.controller;

import com.motofix.api.exception.DuplicateResourceException;
import com.motofix.api.exception.ResourceNotFoundException;
import com.motofix.api.model.Client;
import com.motofix.api.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Listar todos los clientes
    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Buscar un cliente por su ID
    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));
    }

    // Registrar un nuevo cliente
    @PostMapping
    public Client createClient(@Valid @RequestBody Client client) {
        // Controlamos que el email no esté repetido
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new DuplicateResourceException("El email '" + client.getEmail() + "' ya está registrado en el sistema.");
        }
        return clientRepository.save(client);
    }

    // Eliminar un cliente del taller
    @DeleteMapping("/{id}")
    public String deleteClient(@PathVariable Long id) {
        // 1. Verificar si el cliente existe antes de intentar borrarlo
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Cliente no encontrado con el ID: " + id));

        // 2. Si existe, intentamos borrarlo
        clientRepository.delete(client);
        
        return "Cliente con ID " + id + " (" + client.getName() + ") ha sido eliminado correctamente del taller.";
    }
}