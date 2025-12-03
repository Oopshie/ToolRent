package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.RentDTO;
import com.Tingeso.ToolRent.Entities.*;
import com.Tingeso.ToolRent.Repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private RentRepository rentRepository;
    @Mock private ToolRepository toolRepository;
    @Mock private KardexRepository kardexRepository;
    @Mock private RateRepository rateRepository;

    @InjectMocks
    private RentService rentService;

    // =============== createRent ===================

    @Test
    void createRent_WhenClientNotFound_ShouldReturnCode() {
        when(clientRepository.findByRut("11-1")).thenReturn(Optional.empty());

        Object result = rentService.createRent("11-1", 1L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("CLIENT_NOT_FOUND");
    }

    @Test
    void createRent_WhenClientRestricted_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(2);   // no activo

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));

        Object result = rentService.createRent("11-1", 1L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("CLIENT_RESTRICTED");
    }

    @Test
    void createRent_WhenClientHasMaxRents_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of(new RentEntity(), new RentEntity(),
                        new RentEntity(), new RentEntity(), new RentEntity()));

        Object result = rentService.createRent("11-1", 1L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("CLIENT_MAX_RENTS");
    }

    @Test
    void createRent_WhenToolAlreadyRentedByClient_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of()); // menos de 5
        when(rentRepository.existsByClientIdAndToolIdAndActiveTrue(1L, 2L))
                .thenReturn(true);

        Object result = rentService.createRent("11-1", 2L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("TOOL_ALREADY_RENTED_BY_CLIENT");
    }

    @Test
    void createRent_WhenToolNotFound_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of());
        when(rentRepository.existsByClientIdAndToolIdAndActiveTrue(1L, 2L))
                .thenReturn(false);
        when(toolRepository.findById(2L)).thenReturn(Optional.empty());

        Object result = rentService.createRent("11-1", 2L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("TOOL_NOT_FOUND");
    }

    @Test
    void createRent_WhenToolNotAvailable_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        ToolEntity tool = new ToolEntity();
        tool.setId(2L);
        tool.setStatus(3);   // no disponible

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of());
        when(rentRepository.existsByClientIdAndToolIdAndActiveTrue(1L, 2L))
                .thenReturn(false);
        when(toolRepository.findById(2L)).thenReturn(Optional.of(tool));

        Object result = rentService.createRent("11-1", 2L,
                LocalDate.now().plusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("TOOL_NOT_AVAILABLE");
    }

    @Test
    void createRent_WhenFinishDateInPast_ShouldReturnCode() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        ToolEntity tool = new ToolEntity();
        tool.setId(2L);
        tool.setStatus(1);

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of());
        when(rentRepository.existsByClientIdAndToolIdAndActiveTrue(1L, 2L))
                .thenReturn(false);
        when(toolRepository.findById(2L)).thenReturn(Optional.of(tool));

        Object result = rentService.createRent("11-1", 2L,
                LocalDate.now().minusDays(1).toString(), "Empleado");

        assertThat(result).isEqualTo("Fecha de devolución inválida");
    }

    @Test
    void createRent_WhenOk_ShouldCreateRentAndKardex() {
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setStatus(1);

        ToolEntity tool = new ToolEntity();
        tool.setId(2L);
        tool.setStatus(1);

        when(clientRepository.findByRut("11-1")).thenReturn(Optional.of(client));
        when(rentRepository.findByClientIdAndActiveTrue(1L))
                .thenReturn(List.of());
        when(rentRepository.existsByClientIdAndToolIdAndActiveTrue(1L, 2L))
                .thenReturn(false);
        when(toolRepository.findById(2L)).thenReturn(Optional.of(tool));
        when(rentRepository.save(any(RentEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Object result = rentService.createRent("11-1", 2L,
                LocalDate.now().plusDays(3).toString(), "Empleado X");

        assertThat(result).isInstanceOf(RentEntity.class);
        RentEntity saved = (RentEntity) result;
        assertThat(saved.getClientId()).isEqualTo(1L);
        assertThat(saved.getToolId()).isEqualTo(2L);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getEmployeeName()).isEqualTo("Empleado X");

        verify(toolRepository).save(argThat(t -> t.getStatus() == 2));
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    // =============== getAllRentsOrdered ===================

    @Test
    void getAllRentsOrdered_ShouldSortLateFirst() {
        LocalDate today = LocalDate.now();

        RentEntity late = new RentEntity();
        late.setId(1L);
        late.setStartDate(today.minusDays(5).toString());
        late.setFinishDate(today.minusDays(2).toString());
        late.setReturnDate(null);
        late.setActive(true);
        late.setClient(new ClientEntity(1L, "11-1", "Cliente", "cliente@test.com", "987654321", 1));
        late.setTool(new ToolEntity(1L, "martillo", "cat", 1000, 1, null));

        RentEntity normal = new RentEntity();
        normal.setId(2L);
        normal.setStartDate(today.minusDays(3).toString());
        normal.setFinishDate(today.plusDays(2).toString());
        normal.setReturnDate(null);
        normal.setActive(true);
        normal.setClient(new ClientEntity(2L, "22-2", "Cliente B", "cliente@test.com", "987654321", 1));
        normal.setTool(new ToolEntity(2L, "taladro", "cat", 2000, 1, null));

        when(rentRepository.findAll()).thenReturn(List.of(normal, late));

        List<RentDTO> result = rentService.getAllRentsOrdered();

        assertThat(result).hasSize(2);
        // primero debe ir el atrasado
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    // =============== returnTool ===================

    @Test
    void returnTool_ShouldProcessReturnCorrectly_NoDamage() {
        RentEntity rent = new RentEntity();
        rent.setId(1L);
        rent.setToolId(5L);
        rent.setStartDate(LocalDate.now().minusDays(3).toString());
        rent.setFinishDate(LocalDate.now().minusDays(1).toString());
        rent.setEmployeeName("Carlos");

        ClientEntity client = new ClientEntity();
        client.setName("Cliente X");
        rent.setClient(client);

        ToolEntity toolEntityInRent = new ToolEntity();
        toolEntityInRent.setName("Taladro");
        rent.setTool(toolEntityInRent);

        ToolEntity tool = new ToolEntity();
        tool.setId(5L);
        tool.setReplacementValue(20000);

        RateEntity rate = new RateEntity();
        rate.setDailyRentalRate(1000);
        rate.setDailyLateFeeRent(500);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(toolRepository.findById(5L)).thenReturn(Optional.of(tool));
        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(rate);
        when(rentRepository.save(any(RentEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RentDTO dto = rentService.returnTool(1L, false, false);

        assertThat(dto.getToolName()).isEqualTo("Taladro");
        assertThat(dto.getEmployeeName()).isEqualTo("Carlos");
        assertThat(dto.isActive()).isFalse();

        verify(rentRepository, times(1)).save(any(RentEntity.class));
        verify(toolRepository, times(1)).save(argThat(t -> t.getStatus() == 1));
        // SOLO movimiento de devolución
        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void returnTool_WhenDamaged_ShouldGenerateRepairMovement() {
        RentEntity rent = new RentEntity();
        rent.setId(1L);
        rent.setToolId(7L);
        rent.setStartDate(LocalDate.now().minusDays(2).toString());
        rent.setFinishDate(LocalDate.now().toString());
        rent.setEmployeeName("Mario");
        rent.setClient(new ClientEntity(1L, "11-1", "Cliente", "cliente@test.com", "987654321", 1));
        rent.setTool(new ToolEntity(7L, "Sierra", "cat", 1000, 2, null));

        ToolEntity tool = new ToolEntity();
        tool.setId(7L);
        tool.setReplacementValue(10000);

        RateEntity rate = new RateEntity();
        rate.setDailyRentalRate(1000);
        rate.setDailyLateFeeRent(200);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(toolRepository.findById(7L)).thenReturn(Optional.of(tool));
        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(rate);
        when(rentRepository.save(any(RentEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        rentService.returnTool(1L, true, false);

        // reparación + devolución
        verify(kardexRepository, times(2)).save(any(KardexEntity.class));
        verify(toolRepository).save(argThat(t -> t.getStatus() == 3));
    }

    @Test
    void returnTool_WhenIrreparable_ShouldGenerateDecommissionMovement() {
        RentEntity rent = new RentEntity();
        rent.setId(1L);
        rent.setToolId(8L);
        rent.setStartDate(LocalDate.now().minusDays(3).toString());
        rent.setFinishDate(LocalDate.now().toString());
        rent.setEmployeeName("Sofia");
        rent.setClient(new ClientEntity(1L,"11-1", "Cliente", "cliente@test.com", "987654321", 1));
        rent.setTool(new ToolEntity(8L, "Taladro", "cat", 1000, 2, null));

        ToolEntity tool = new ToolEntity();
        tool.setId(8L);
        tool.setReplacementValue(50000);

        RateEntity rate = new RateEntity();
        rate.setDailyRentalRate(1500);
        rate.setDailyLateFeeRent(500);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(toolRepository.findById(8L)).thenReturn(Optional.of(tool));
        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(rate);
        when(rentRepository.save(any(RentEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        rentService.returnTool(1L, false, true);

        // baja + devolución
        verify(kardexRepository, times(2)).save(any(KardexEntity.class));
        verify(toolRepository).save(argThat(t -> t.getStatus() == 4));
    }

    @Test
    void returnTool_WhenRateMissing_ShouldThrow() {
        RentEntity rent = new RentEntity();
        rent.setId(1L);
        rent.setToolId(9L);
        rent.setStartDate(LocalDate.now().minusDays(1).toString());
        rent.setFinishDate(LocalDate.now().toString());
        rent.setEmployeeName("Empleado");

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(toolRepository.findById(9L)).thenReturn(Optional.of(new ToolEntity()));
        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(null);

        assertThatThrownBy(() -> rentService.returnTool(1L, false, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rate missing");
    }

    // =============== getAll ===================

    @Test
    void getAll_ShouldReturnListFromRepository() {
        when(rentRepository.findAll()).thenReturn(List.of(new RentEntity(), new RentEntity()));

        List<RentEntity> result = rentService.getAll();

        assertThat(result).hasSize(2);
        verify(rentRepository).findAll();
    }
}
