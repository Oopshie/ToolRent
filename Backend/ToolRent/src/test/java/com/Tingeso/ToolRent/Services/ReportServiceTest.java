package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.ActiveRentReportDTO;
import com.Tingeso.ToolRent.Entities.ClientEntity;
import com.Tingeso.ToolRent.Entities.RentEntity;
import com.Tingeso.ToolRent.Entities.ToolEntity;
import com.Tingeso.ToolRent.Repositories.RentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private RentRepository rentRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ============================================================
    // 1) getActiveRents()
    // ============================================================
    @Test
    void getActiveRents_ShouldMapRentsToDTO_AndCalculateLateFlag() {
        // Cliente y herramienta
        ClientEntity client = new ClientEntity(
                1L,
                "11.111.111-1",
                "Juan Pérez",
                "juan@example.com",
                "123456789",
                1
        );

        ToolEntity tool = new ToolEntity(
                10L,
                "martillo",
                "manual",
                10000,
                1,
                null
        );

        // Arriendo activo con finishDate = hoy (no atrasado)
        RentEntity rent = new RentEntity();
        rent.setId(5L);
        rent.setClientId(client.getId());
        rent.setToolId(tool.getId());
        rent.setStartDate(LocalDate.now().minusDays(2).toString());
        rent.setFinishDate(LocalDate.now().toString());  // == today → no late
        rent.setReturnDate(null);
        rent.setActive(true);
        rent.setFineAmount(0);
        rent.setTotalAmount(0);
        rent.setDamaged(false);
        rent.setIrreparable(false);
        rent.setEmployeeName("Empleado 1");
        rent.setClient(client);
        rent.setTool(tool);

        when(rentRepository.findByActiveTrue()).thenReturn(List.of(rent));

        // Act
        List<ActiveRentReportDTO> result = reportService.getActiveRents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ActiveRentReportDTO dto = result.get(0);
        assertEquals(rent.getId(), dto.rentId);
        assertEquals("Juan Pérez", dto.clientName);
        assertEquals("martillo", dto.toolName);
        assertEquals(rent.getStartDate(), dto.startDate);
        assertEquals(rent.getFinishDate(), dto.finishDate);
        // Como finishDate == today, late debe ser false
        assertFalse(dto.late);

        verify(rentRepository, times(1)).findByActiveTrue();
    }

    // ============================================================
    // 2) getLateClients()
    // ============================================================
    @Test
    void getLateClients_ShouldReturnRepositoryResult() {
        List<Map<String, Object>> repoResult = new ArrayList<>();

        Map<String, Object> row = new HashMap<>();
        row.put("clientname", "Juan Pérez");
        row.put("rentid", 5L);
        row.put("finishdate", "2025-11-28");

        repoResult.add(row);

        when(rentRepository.findLateClients(anyString())).thenReturn(repoResult);

        // Act
        List<Map<String, Object>> result = reportService.getLateClients();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).get("clientname"));
        assertEquals(5L, result.get(0).get("rentid"));

        verify(rentRepository, times(1)).findLateClients(anyString());
    }

    // ============================================================
    // 3) getToolRanking()
    // ============================================================
    @Test
    void getToolRanking_ShouldNormalizeKeysToCamelCase() {
        // Simulamos la respuesta "raw" del repositorio
        Map<String, Object> row = new HashMap<>();
        row.put("toolname", "Taladro");
        row.put("timesused", 7L);

        List<Map<String, Object>> raw = List.of(row);

        when(rentRepository.getToolRanking()).thenReturn(raw);

        // Act
        List<Map<String, Object>> result = reportService.getToolRanking();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        Map<String, Object> mapped = result.get(0);
        assertTrue(mapped.containsKey("toolName"));
        assertTrue(mapped.containsKey("timesUsed"));
        assertEquals("Taladro", mapped.get("toolName"));
        assertEquals(7L, mapped.get("timesUsed"));

        verify(rentRepository, times(1)).getToolRanking();
    }
}
