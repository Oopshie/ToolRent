package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Tingeso.ToolRent.Repositories.ToolRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private KardexService kardexService;

    @InjectMocks
    private ToolService toolService;

    @Test
    void calcularDisponiblesPorNombre_ConListaVacia_DeberiaRetornarMapVacio() {
        // Given
        List<ToolEntity> herramientas = Collections.emptyList();

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void calcularDisponiblesPorNombre_ConTodasNoDisponibles_DeberiaRetornarMapVacio() {
        // Given
        List<ToolEntity> herramientas = Arrays.asList(
                new ToolEntity(1L, "martillo", "manual", 10000, 2),  // Prestada
                new ToolEntity(2L, "taladro", "electrico", 50000, 3), // En reparación
                new ToolEntity(3L, "sierra", "electrico", 30000, 4)   // Dada de baja
        );

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void calcularDisponiblesPorNombre_ConVariasDisponiblesMismoNombre_DeberiaContarCorrectamente() {
        // Given
        List<ToolEntity> herramientas = Arrays.asList(
                new ToolEntity(1L, "martillo", "manual", 10000, 1),  // Disponible
                new ToolEntity(2L, "martillo", "manual", 10000, 1),  // Disponible
                new ToolEntity(3L, "martillo", "manual", 10000, 1)   // Disponible
        );

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get("martillo"));
    }

    @Test
    void calcularDisponiblesPorNombre_ConMezclaDisponiblesYNoDisponibles_DeberiaContarSoloDisponibles() {
        // Given
        List<ToolEntity> herramientas = Arrays.asList(
                new ToolEntity(1L, "martillo", "manual", 10000, 1),  // Disponible
                new ToolEntity(2L, "martillo", "manual", 10000, 1),  // Disponible
                new ToolEntity(3L, "martillo", "manual", 10000, 2),  // Prestada
                new ToolEntity(4L, "martillo", "manual", 10000, 3),  // En reparación
                new ToolEntity(5L, "taladro", "electrico", 50000, 1), // Disponible
                new ToolEntity(6L, "taladro", "electrico", 50000, 4)  // Dada de baja
        );

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertEquals(2, resultado.size());
        assertEquals(2L, resultado.get("martillo"));
        assertEquals(1L, resultado.get("taladro"));
        assertFalse(resultado.containsKey("sierra"));  // No hay ninguna sierra
    }

    @Test
    void calcularDisponiblesPorNombre_ConNombresDistintos_DeberiaCrearMultiplesEntradas() {
        // Given
        List<ToolEntity> herramientas = Arrays.asList(
                new ToolEntity(1L, "martillo", "manual", 10000, 1),
                new ToolEntity(2L, "destornillador", "manual", 5000, 1),
                new ToolEntity(3L, "taladro", "electrico", 50000, 1),
                new ToolEntity(4L, "sierra", "electrico", 30000, 1),
                new ToolEntity(5L, "llave inglesa", "manual", 8000, 1)
        );

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertEquals(5, resultado.size());
        assertEquals(1L, resultado.get("martillo"));
        assertEquals(1L, resultado.get("destornillador"));
        assertEquals(1L, resultado.get("taladro"));
        assertEquals(1L, resultado.get("sierra"));
        assertEquals(1L, resultado.get("llave inglesa"));
    }

    @Test
    void calcularDisponiblesPorNombre_CasoCompleto_DeberiaFuncionarCorrectamente() {
        // Given - Caso complejo de ejemplo del issue
        List<ToolEntity> herramientas = Arrays.asList(
                new ToolEntity(1L, "martillo", "manual", 10000, 1),       // Disponible
                new ToolEntity(2L, "martillo", "manual", 10000, 1),       // Disponible
                new ToolEntity(3L, "destornillador", "manual", 5000, 1),  // Disponible
                new ToolEntity(4L, "martillo", "manual", 10000, 0),       // No disponible (status 0)
                new ToolEntity(5L, "taladro", "electrico", 50000, 2)      // No disponible (prestada)
        );

        // When
        Map<String, Long> resultado = toolService.calcularDisponiblesPorNombre(herramientas);

        // Then
        assertEquals(2L, resultado.get("martillo").longValue());
        assertEquals(1L, resultado.get("destornillador").longValue());
        assertFalse(resultado.containsKey("taladro"));
    }
}
