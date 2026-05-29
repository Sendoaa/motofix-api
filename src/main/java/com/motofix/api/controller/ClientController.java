package com.motofix.api.controller;

import com.motofix.api.dto.ClientCreateDTO;
import com.motofix.api.dto.ClientDTO;
import com.motofix.api.model.Client;
import com.motofix.api.repository.ClientRepository;
import com.motofix.api.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // 1. Listar todos los clientes
    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Buscar cliente por ID
    @GetMapping("/{id}")
    public ClientDTO getClientById(@PathVariable Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return convertToDTO(client);
    }

    // 3. Crear un nuevo cliente
    @PostMapping
    public ClientDTO createClient(@Valid @RequestBody ClientCreateDTO createDTO) {
        Client client = new Client();
        client.setName(createDTO.getName());
        client.setPhone(createDTO.getPhone());
        client.setEmail(createDTO.getEmail());

        Client savedClient = clientRepository.save(client);
        return convertToDTO(savedClient);
    }

    // 4. Editar un cliente existente
    @PutMapping("/{id}")
    public ClientDTO updateClient(@PathVariable Long id, @Valid @RequestBody ClientCreateDTO updateDTO) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(updateDTO.getName());
                    client.setPhone(updateDTO.getPhone());
                    client.setEmail(updateDTO.getEmail());

                    Client updatedClient = clientRepository.save(client);
                    return convertToDTO(updatedClient);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede actualizar: Cliente no encontrado con ID: " + id));
    }

    // 5. Eliminar un cliente
    @DeleteMapping("/{id}")
    public String deleteClient(@PathVariable Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede eliminar: Cliente no encontrado con ID: " + id));

        clientRepository.delete(client);
        return "El cliente " + client.getName() + " ha sido eliminado correctamente del sistema.";
    }

    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setPhone(client.getPhone());
        dto.setEmail(client.getEmail());
        return dto;
    }
}