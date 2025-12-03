package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.Entities.ClientEntity;
import com.Tingeso.ToolRent.Repositories.ClientRepository;
import com.Tingeso.ToolRent.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin
public class ClientController {
    @Autowired
    ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @PostMapping("/")
    public ResponseEntity<ClientEntity> addClient(@RequestBody ClientEntity client) {
        ClientEntity clientNew = clientService.addClient(client);
        return ResponseEntity.ok(clientNew);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/clients")
    public ResponseEntity<java.util.List<ClientEntity>> listClients() {
        java.util.List<ClientEntity> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientEntity> getClientById(@PathVariable Long id) {
        ClientEntity client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/getByRut/{rut}")
    public ResponseEntity<ClientEntity> getClientByRut(@PathVariable String rut) {
        ClientEntity client = clientService.getStatusByRut(rut);
        return ResponseEntity.ok(client);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @PutMapping("/")
    public ResponseEntity<ClientEntity> updateClient(@RequestBody ClientEntity client) {
        ClientEntity clientUpdated = clientService.updateClient(client);
        return ResponseEntity.ok(clientUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteClientById(@PathVariable Long id) throws Exception {
        var isDeleted = clientService.deleteClient(id);
        return  ResponseEntity.noContent().build();
    }




}
