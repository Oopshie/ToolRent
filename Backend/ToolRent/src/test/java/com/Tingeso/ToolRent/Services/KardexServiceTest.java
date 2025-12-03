package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.KardexDTO;
import com.Tingeso.ToolRent.Entities.KardexEntity;
import com.Tingeso.ToolRent.Repositories.KardexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KardexServiceTest {

    @Mock
    private KardexRepository kardexRepository;

    @InjectMocks
    private KardexService kardexService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerMovement_ShouldSaveMovementCorrectly() {
        kardexService.registerMovement(4, 10L, "Alex");

        verify(kardexRepository, times(1)).save(any(KardexEntity.class));
    }

    @Test
    void getByTool_ShouldReturnListOfDtos() {

        KardexEntity mov = new KardexEntity(
                1L, 4, "2024-12-01T10:00", 1, 5L, "Alex", null
        );

        when(kardexRepository.findByToolIdOrderByMovementDateDesc(5L))
                .thenReturn(List.of(mov));

        List<KardexDTO> result = kardexService.getByTool(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToolId()).isEqualTo(5L);
        assertThat(result.get(0).getMovementType()).isEqualTo(4);
    }

    @Test
    void getByDateRange_ShouldReturnDtos() {

        KardexEntity mov = new KardexEntity(
                1L, 3, "2024-12-02T12:00", 1, 2L, "Maria", null
        );

        when(kardexRepository.findByMovementDateBetweenOrderByMovementDateDesc(
                "2024-12-01", "2024-12-31"
        )).thenReturn(List.of(mov));

        List<KardexDTO> result = kardexService.getByDateRange("2024-12-01", "2024-12-31");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMovementType()).isEqualTo(3);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Maria");
    }

    @Test
    void getAllMovements_ShouldReturnAllDtos() {

        KardexEntity mov1 = new KardexEntity(1L, 1, "2024-12-01", 1, 4L, "Alex", null);
        KardexEntity mov2 = new KardexEntity(2L, 2, "2024-12-02", 1, 4L, "Alex", null);

        when(kardexRepository.findAll()).thenReturn(List.of(mov1, mov2));

        List<KardexDTO> result = kardexService.getAllMovements();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getMovementType()).isEqualTo(2);
    }
}
