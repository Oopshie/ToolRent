package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import com.Tingeso.ToolRent.Repositories.ToolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private KardexService kardexService;

    @InjectMocks
    private ToolService toolService;

    private ToolEntity baseTool;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(toolService, "toolRepository", toolRepository);
        ReflectionTestUtils.setField(toolService, "kardexService", kardexService);

        baseTool = new ToolEntity(
                1L, "Martillo", "Manual", 10000, 1, null
        );
    }

    // =====================================================
    // addTool
    // =====================================================
    @Test
    void addTool_ShouldNormalizeAndSetStatusAndRegisterKardex() {
        ToolEntity toSave = new ToolEntity(null, "  Martillo  ", "  Manual  ", 10000, 0, null);
        ToolEntity saved = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);

        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.addTool(toSave, "Empleado Test");

        assertEquals("martillo", result.getName());
        assertEquals("manual", result.getCategory());
        assertEquals(1, result.getStatus());

        verify(toolRepository).save(any());
        verify(kardexService).registerMovement(4, 1L, "Empleado Test");
    }

    // =====================================================
    // getToolsByName
    // =====================================================
    @Test
    void getToolsByName_ShouldDelegateToRepository() {
        when(toolRepository.findByNameContainingIgnoreCase("martillo"))
                .thenReturn(List.of(baseTool));

        List<ToolEntity> result = toolService.getToolsByName("martillo");

        assertEquals(1, result.size());
        verify(toolRepository).findByNameContainingIgnoreCase("martillo");
    }

    // =====================================================
    // getToolById
    // =====================================================
    @Test
    void getToolById_WhenExists_ShouldReturn() {
        when(toolRepository.findById(1L)).thenReturn(Optional.of(baseTool));

        ToolEntity result = toolService.getToolById(1L);

        assertNotNull(result);
        verify(toolRepository).findById(1L);
    }

    @Test
    void getToolById_WhenNotExists_ShouldThrow() {
        when(toolRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> toolService.getToolById(99L));

        assertTrue(ex.getMessage().contains("Tool not found"));
    }

    // =====================================================
    // getAllTools FIX
    // =====================================================
    @Test
    void getAllTools_ShouldReturnList() {
        // FIX: devolver ArrayList REAL, no List.of()
        when(toolRepository.findAll()).thenReturn(new ArrayList<>(List.of(baseTool)));

        List<ToolEntity> result = toolService.getAllTools();

        assertEquals(1, result.size());
    }

    // =====================================================
    // getToolsByCategory FIX
    // =====================================================
    @Test
    void getToolsByCategory_ShouldReturnList() {
        when(toolRepository.findByCategory("manual"))
                .thenReturn(new ArrayList<>(List.of(baseTool)));

        List<ToolEntity> result = toolService.getToolsByCategory("manual");

        assertEquals(1, result.size());
        verify(toolRepository).findByCategory("manual");
    }

    // =====================================================
    // updateToolFields
    // =====================================================
    @Test
    void updateToolFields_ShouldUpdateNameCategoryAndValue_NotStatus() {
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        ToolEntity incoming = new ToolEntity(1L, "martillo pro", "manual", 20000, 99, null);
        ToolEntity saved = new ToolEntity(1L, "martillo pro", "manual", 20000, 1, null);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.updateToolFields(incoming);

        assertEquals("martillo pro", result.getName());
        assertEquals(20000, result.getReplacementValue());
    }

    // =====================================================
    // updateToolGroupValues
    // =====================================================
    @Test
    void updateToolGroupValues_ShouldUpdateAllGroup() {
        ToolEntity t1 = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        ToolEntity t2 = new ToolEntity(2L, "martillo", "manual", 10000, 1, null);

        List<ToolEntity> group = new ArrayList<>(List.of(t1, t2));

        when(toolRepository.findByNameAndCategory("martillo", "manual"))
                .thenReturn(group);

        toolService.updateToolGroupValues("Martillo", "Manual", 20000);

        assertEquals(20000, t1.getReplacementValue());
        verify(toolRepository).saveAll(group);
    }

    // =====================================================
    // updateToolStatus
    // =====================================================
    @Test
    void updateToolStatus_ToRepair_ShouldRegisterMovement5() {
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        ToolEntity saved = new ToolEntity(1L, "martillo", "manual", 10000, 3, null);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.updateToolStatus(1L, 3, "Empleado Test");

        assertEquals(3, result.getStatus());
        verify(kardexService).registerMovement(5, 1L, "Empleado Test");
    }

    @Test
    void updateToolStatus_FromRepairToAvailable_ShouldRegisterMovement4() {
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 3, null);
        ToolEntity saved = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);

        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.updateToolStatus(1L, 1, "Empleado Test");

        assertEquals(1, result.getStatus());
        verify(kardexService).registerMovement(4, 1L, "Empleado Test");
    }

    @Test
    void updateToolStatus_ToDecommissioned_ShouldRegisterMovement3() {
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        ToolEntity saved = new ToolEntity(1L, "martillo", "manual", 10000, 4, null
        );

        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.updateToolStatus(1L, 4, "Empleado Test");

        assertEquals(4, result.getStatus());
        verify(kardexService).registerMovement(3, 1L, "Empleado Test");
    }

    // =====================================================
    // deactivateTool
    // =====================================================
    @Test
    void deactivateTool_ShouldSetStatus4() {
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        when(toolRepository.findById(1L)).thenReturn(Optional.of(existing));

        ToolEntity saved = new ToolEntity(1L, "martillo", "manual", 10000, 4, null);
        when(toolRepository.save(any())).thenReturn(saved);

        ToolEntity result = toolService.deactivateTool(1L);

        assertEquals(4, result.getStatus());
    }

    // =====================================================
    // deleteToolById
    // =====================================================
    @Test
    void deleteToolById_ShouldCallRepository() {
        toolService.deleteToolById(1L);
        verify(toolRepository).deleteById(1L);
    }

    // =====================================================
    // checkDuplicateAndSuggestPrice
    // =====================================================
    @Test
    void checkDuplicateAndSuggestPrice_WhenExists_ShouldReturnExistsTrue() {
        ToolEntity t1 = new ToolEntity(1L, "martillo", "manual", 15000, 1, null);

        when(toolRepository.findByNameAndCategory("martillo", "manual"))
                .thenReturn(List.of(t1));

        Map<String, Object> result = toolService.checkDuplicateAndSuggestPrice("Martillo", "Manual");

        assertEquals(true, result.get("exists"));
        assertEquals(15000, result.get("suggestedPrice"));
    }

    @Test
    void checkDuplicateAndSuggestPrice_WhenNotExists_ShouldReturnExistsFalse() {
        when(toolRepository.findByNameAndCategory("martillo", "manual"))
                .thenReturn(Collections.emptyList());

        Map<String, Object> result = toolService.checkDuplicateAndSuggestPrice("Martillo", "Manual");

        assertEquals(false, result.get("exists"));
        assertNull(result.get("suggestedPrice"));
    }

    // =====================================================
    // getToolsByStatus
    // =====================================================
    @Test
    void getToolsByStatus_ShouldDelegateToRepository() {
        when(toolRepository.findByStatus(1)).thenReturn(List.of(baseTool));

        List<ToolEntity> result = toolService.getToolsByStatus(1);

        assertEquals(1, result.size());
    }

    // =====================================================
    // updateReplacementValueForGroup
    // =====================================================
    @Test
    void updateReplacementValueForGroup_ShouldUpdateAllAndSaveAll() {
        ToolEntity t1 = new ToolEntity(1L, "martillo", "manual", 10000, 1, null);
        ToolEntity t2 = new ToolEntity(2L, "martillo", "manual", 10000, 1, null);

        List<ToolEntity> tools = new ArrayList<>(List.of(t1, t2));

        when(toolRepository.findByNameAndCategory("martillo", "manual"))
                .thenReturn(tools);

        toolService.updateReplacementValueForGroup("Martillo", "Manual", 30000);

        assertEquals(30000, t1.getReplacementValue());
        verify(toolRepository).saveAll(tools);
    }

    @Test
    void calcularDisponiblesPorNombre_ShouldReturnCorrectCounts() throws Exception {

        // ⚙️ 1) Preparamos una lista con herramientas mixtas
        List<ToolEntity> herramientas = List.of(
                new ToolEntity(1L, "martillo", "manual", 10000, 1, null), // disponible
                new ToolEntity(2L, "martillo", "manual", 10000, 2, null), // prestada
                new ToolEntity(3L, "martillo", "manual", 10000, 1, null), // disponible
                new ToolEntity(4L, "taladro", "electrica", 20000, 1, null), // disponible
                new ToolEntity(5L, "taladro", "electrica", 20000, 3, null)  // en reparación
        );

        // ⚙️ 2) Obtenemos el método privado mediante reflexión
        var method = ToolService.class.getDeclaredMethod(
                "calcularDisponiblesPorNombre",
                List.class
        );
        method.setAccessible(true); // permitir acceso

        // ⚙️ 3) Ejecutamos el método privado
        Map<String, Long> resultado =
                (Map<String, Long>) method.invoke(toolService, herramientas);

        // ⚙️ 4) Verificación
        assertEquals(2L, resultado.get("martillo")); // hay 2 disponibles
        assertEquals(1L, resultado.get("taladro"));  // hay 1 disponible
        assertEquals(2, resultado.size());
    }


}
