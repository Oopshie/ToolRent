package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.ClientEntity;
import com.Tingeso.ToolRent.Repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests de ClientService usando JUnit 5 + AssertJ + Mockito
 */
@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private ClientEntity client;

    @BeforeEach
    void setUp() {
        client = new ClientEntity();
        client.setId(1L);
        client.setRut("12345678-5"); // RUT válido con tu método
        client.setName("Juan Pérez");
        client.setEmail("test@mail.com");
        client.setPhoneNumber("123456789");
        client.setStatus(1);
    }

    // =========================
    // addClient
    // =========================

    @Test
    void addClient_withValidRutAndNewClient_savesAndReturnsClient() {
        // Arrange
        when(clientRepository.findByRut("12345678-5")).thenReturn(Optional.empty());
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(client);

        // Act
        ClientEntity result = clientService.addClient(client);

        // Assert (usando JUnit)
        assertEquals(client.getRut(), result.getRut());
        assertEquals(client.getName(), result.getName());
        // Y AssertJ para chequear que no es nulo
        assertThat(result).isNotNull();
        verify(clientRepository).save(client);
    }

    @Test
    void addClient_withInvalidRut_throwsRuntimeException() {
        // Arrange
        client.setRut("12345678-9"); // RUT inválido

        // Assert (AssertJ)
        assertThatThrownBy(() -> clientService.addClient(client))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RUT inválido");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void addClient_withExistingRut_throwsRuntimeException() {
        // Arrange
        when(clientRepository.findByRut("12345678-5")).thenReturn(Optional.of(client));

        // Act & Assert (JUnit)
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clientService.addClient(client));

        assertEquals("El cliente con RUT 12345678-5 ya existe", ex.getMessage());
        verify(clientRepository, never()).save(any());
    }

    // =========================
    // getClientById
    // =========================

    @Test
    void getClientById_whenExists_returnsClient() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        ClientEntity result = clientService.getClientById(1L);

        // Assert (AssertJ)
        assertThat(result)
                .isNotNull()
                .extracting(ClientEntity::getId)
                .isEqualTo(1L);
    }

    @Test
    void getClientById_whenNotFound_throwsRuntimeException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> clientService.getClientById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Client not found with id: 1");
    }

    // =========================
    // getAllClients
    // =========================

    @Test
    void getAllClients_returnsArrayList() {
        // IMPORTANTE: el servicio castea a (ArrayList) lo que retorna findAll(),
        // así que aquí debemos retornar realmente un ArrayList para evitar ClassCastException
        ArrayList<ClientEntity> clients = new ArrayList<>();
        clients.add(client);

        when(clientRepository.findAll()).thenReturn(clients);

        // Act
        ArrayList<ClientEntity> result = clientService.getAllClients();

        // Assert
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(client);
    }

    // =========================
    // getStatusByRut
    // =========================

    @Test
    void getStatusByRut_whenExists_returnsClient() {
        when(clientRepository.findByRut("12345678-5")).thenReturn(Optional.of(client));

        ClientEntity result = clientService.getStatusByRut("12345678-5");

        assertThat(result).isNotNull();
        assertEquals(1, result.getStatus());
    }

    @Test
    void getStatusByRut_whenNotFound_throwsRuntimeException() {
        when(clientRepository.findByRut("12345678-5")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getStatusByRut("12345678-5"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado");;;
    }

    // =========================
    // updateClient
    // =========================

    @Test
    void updateClient_withValidRutAndExistingClient_savesAndReturns() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(ClientEntity.class))).thenReturn(client);

        ClientEntity updated = clientService.updateClient(client);

        assertThat(updated).isNotNull();
        verify(clientRepository).save(client);
    }

    @Test
    void updateClient_withInvalidRut_throwsRuntimeException() {
        client.setRut("12345678-9"); // inválido

        assertThatThrownBy(() -> clientService.updateClient(client))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RUT inválido");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_whenNotFound_throwsRuntimeException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(client))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    // =========================
    // changeClientStatus
    // =========================

    @Test
    void changeClientStatus_whenExists_updatesStatusAndSaves() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(ClientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientEntity result = clientService.changeClientStatus(1L, 0);

        assertThat(result.getStatus()).isEqualTo(0);
        verify(clientRepository).save(client);
    }

    @Test
    void changeClientStatus_whenNotFound_throwsRuntimeException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.changeClientStatus(1L, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    // =========================
    // deleteClient
    // =========================

    @Test
    void deleteClient_whenExists_deletesAndReturnsClient() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientEntity deleted = clientService.deleteClient(1L);

        assertThat(deleted).isEqualTo(client);
        verify(clientRepository).delete(client);
    }

    @Test
    void deleteClient_whenNotFound_throwsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class,
                () -> clientService.deleteClient(1L));

        assertThat(ex.getMessage()).contains("Client not found with id: 1");
        verify(clientRepository, never()).delete(any());
    }

    // =========================
    // isValidRut (tests directos)
    // =========================

    @Test
    void isValidRut_withCorrectRut_returnsTrue() {
        boolean valid = clientService.isValidRut("12345678-5");
        assertThat(valid).isTrue();
    }

    @Test
    void isValidRut_withIncorrectRut_returnsFalse() {
        boolean valid = clientService.isValidRut("12345678-9");
        assertThat(valid).isFalse();
    }
}
