package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.RateEntity;
import com.Tingeso.ToolRent.Repositories.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RateServiceTest {

    @Mock
    private RateRepository rateRepository;

    @InjectMocks
    private RateService rateService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===========================================================
    // TEST: getLatestRate()
    // ===========================================================
    @Test
    void getLatestRate_ShouldReturnLatestRate() {
        RateEntity rate = new RateEntity(1L, 3000, 500);

        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(rate);

        RateEntity result = rateService.getLatestRate();

        assertNotNull(result);
        assertEquals(3000, result.getDailyRentalRate());
        assertEquals(500, result.getDailyLateFeeRent());

        verify(rateRepository).findTopByOrderByIdDesc();
    }

    // ===========================================================
    // TEST: createRate()
    // ===========================================================
    @Test
    void createRate_ShouldCreateAndSaveRate() {
        RateEntity saved = new RateEntity(1L, 4000, 800);

        when(rateRepository.save(any(RateEntity.class))).thenReturn(saved);

        RateEntity result = rateService.createRate(4000, 800);

        assertNotNull(result);
        assertEquals(4000, result.getDailyRentalRate());
        assertEquals(800, result.getDailyLateFeeRent());

        verify(rateRepository).save(any(RateEntity.class));
    }

    // ===========================================================
    // TEST: addRate()
    // ===========================================================
    @Test
    void addRate_ShouldSaveGivenRate() {
        RateEntity rate = new RateEntity(1L, 5000, 1000);

        when(rateRepository.save(rate)).thenReturn(rate);

        RateEntity result = rateService.addRate(rate);

        assertNotNull(result);
        assertEquals(5000, result.getDailyRentalRate());
        assertEquals(1000, result.getDailyLateFeeRent());

        verify(rateRepository).save(rate);
    }

    // ===========================================================
    // TEST EXTRA → para cubrir método oculto findTopByOrderByIdDesc()
    // ===========================================================
    @Test
    void findTopByOrderByIdDesc_ShouldDelegateToRepository() {

        RateEntity rate = new RateEntity(2L, 6000, 1200);
        when(rateRepository.findTopByOrderByIdDesc()).thenReturn(rate);

        // Llamar al método duplicado
        RateEntity result = rateService.findTopByOrderByIdDesc();

        assertNotNull(result);
        assertEquals(6000, result.getDailyRentalRate());
        assertEquals(1200, result.getDailyLateFeeRent());

        verify(rateRepository).findTopByOrderByIdDesc();
    }
}
