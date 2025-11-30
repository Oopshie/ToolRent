package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.ClientEntity;
import com.Tingeso.ToolRent.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientService {
    @Autowired
    ClientRepository clientRepository;

    //CRUD
    public ClientEntity addClient(ClientEntity client) {
        // validar rut
        if (!isValidRut(client.getRut())) {
            throw new RuntimeException("RUT inválido");
        }

        // verificar si el cliente ya existe
        if (clientRepository.findByRut(client.getRut()).isPresent()) {
            throw new RuntimeException("El cliente con RUT " + client.getRut() + " ya existe");
        }

        // guardar cliente
        return clientRepository.save(client);
    }

    public ClientEntity getClientById(Long id){
        return clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public ArrayList<ClientEntity> getAllClients(){

        return (ArrayList<ClientEntity>)clientRepository.findAll();
    }

    public ClientEntity getStatusByRut(String rut) {
        return clientRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public ClientEntity updateClient(ClientEntity client){
        // validar rut
        if (!isValidRut(client.getRut())) {
            throw new RuntimeException("RUT inválido");
        }
        // verificar si el cliente existe
        ClientEntity existingClient = clientRepository.findById(client.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return clientRepository.save(client);
    }

    public ClientEntity changeClientStatus(Long clientId, int newStatus){
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        client.setStatus(newStatus);
        return clientRepository.save(client);

    }

    public ClientEntity deleteClient(Long id) throws Exception {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new Exception("Client not found with id: " + id));
        clientRepository.delete(client);
        return client;
    }

    public boolean isValidRut(String rutCompleto) {
        if (rutCompleto == null || rutCompleto.trim().isEmpty()) return false;

        // Limpia puntos y guión
        rutCompleto = rutCompleto.replace(".", "").replace("-", "").toUpperCase();

        // Debe tener al menos 8 caracteres
        if (rutCompleto.length() < 8) return false;

        // Separa cuerpo y dígito verificador
        String cuerpo = rutCompleto.substring(0, rutCompleto.length() - 1);
        char dv = rutCompleto.charAt(rutCompleto.length() - 1);

        // Calcula el dígito verificador esperado
        int suma = 0;
        int multiplo = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
            multiplo = (multiplo == 7) ? 2 : multiplo + 1;
        }

        int resto = 11 - (suma % 11);
        char dvEsperado;

        if (resto == 11) {
            dvEsperado = '0';
        } else if (resto == 10) {
            dvEsperado = 'K';
        } else {
            dvEsperado = (char) (resto + '0');
        }

        // Comparar DV ingresado con calculado
        return dv == dvEsperado;

    }
}
