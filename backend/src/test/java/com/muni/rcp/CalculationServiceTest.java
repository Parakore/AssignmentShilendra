package com.muni.rcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muni.rcp.config.JsonRateProvider;
import com.muni.rcp.dto.CalculationInputDTO;
import com.muni.rcp.dto.CalculationResultDTO;
import com.muni.rcp.exception.InvalidRoadTypeException;
import com.muni.rcp.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CalculationServiceTest {

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        JsonRateProvider rateProvider = new JsonRateProvider(
                new DefaultResourceLoader(),
                new ObjectMapper(),
                "classpath:rates-config.json"
        );
        rateProvider.loadConfiguration();
        calculationService = new CalculationService(rateProvider, "K7Q2");
    }

    @Test
    void testWorkedExampleA_Dehradun() {
        CalculationInputDTO input = new CalculationInputDTO(
                "dehradun",
                "BT",
                new BigDecimal("12.5"),
                new BigDecimal("1.2"),
                6,
                "PRIVATE",
                LocalDate.of(2026, 3, 2),
                LocalDate.of(2026, 3, 1)
        );

        CalculationResultDTO result = calculationService.calculateFee(input);

        assertNotNull(result);
        assertEquals(15L, result.getAreaInSqm(), "Area must be ceil(12.5 * 1.2) = 15");
        assertEquals(new BigDecimal("18000.00"), result.getRestorationCharge(), "Restoration: 15 * 1200 = 18,000");
        assertEquals(new BigDecimal("1350.00"), result.getPermissionFee(), "Permission: 15 * 15 * 6 = 1,350");
        assertEquals(new BigDecimal("135.00"), result.getUrgencySurcharge(), "Surcharge: 10% of 1350 = 135");
        assertEquals(new BigDecimal("5000.00"), result.getSecurityDeposit(), "Deposit: max(5000, 25% of 18000 = 4500) = 5,000");
        assertEquals(new BigDecimal("24485.00"), result.getTotalAmount(), "Total: 18000 + 1350 + 135 + 5000 = 24,485");
        assertEquals("K7Q2", result.getReviewRef(), "ReviewRef must match Addendum 3.1");
    }

    @Test
    void testWorkedExampleB_Haridwar() {
        CalculationInputDTO input = new CalculationInputDTO(
                "haridwar",
                "BT",
                new BigDecimal("12.5"),
                new BigDecimal("1.2"),
                6,
                "PRIVATE",
                LocalDate.of(2026, 3, 2),
                LocalDate.of(2026, 3, 1)
        );

        CalculationResultDTO result = calculationService.calculateFee(input);

        assertNotNull(result);
        assertEquals(15L, result.getAreaInSqm(), "Area must be 15");
        assertEquals(new BigDecimal("18000.00"), result.getRestorationCharge(), "Restoration: 15 * 1200 = 18,000");
        assertEquals(new BigDecimal("1800.00"), result.getPermissionFee(), "Permission: 15 * 20 * 6 = 1,800");
        assertEquals(new BigDecimal("180.00"), result.getUrgencySurcharge(), "Surcharge: 10% of 1800 = 180");
        assertEquals(new BigDecimal("7500.00"), result.getSecurityDeposit(), "Deposit: max(7500, 4500) = 7,500");
        assertEquals(new BigDecimal("27480.00"), result.getTotalAmount(), "Total: 18000 + 1800 + 180 + 7500 = 27,480");
        assertEquals("K7Q2", result.getReviewRef());
    }

    @Test
    void testInactiveRoadType_Kutcha_ThrowsException() {
        CalculationInputDTO input = new CalculationInputDTO(
                "dehradun",
                "KUTCHA",
                new BigDecimal("10.0"),
                new BigDecimal("2.0"),
                5,
                "PRIVATE",
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 1)
        );

        InvalidRoadTypeException ex = assertThrows(InvalidRoadTypeException.class,
                () -> calculationService.calculateFee(input));

        assertTrue(ex.getMessage().contains("not active"), "Message should mention not active: " + ex.getMessage());
    }

    @Test
    void testUnknownRoadType_ThrowsException() {
        CalculationInputDTO input = new CalculationInputDTO(
                "dehradun",
                "UNKNOWN_GRAVEL",
                new BigDecimal("10.0"),
                new BigDecimal("2.0"),
                5,
                "PRIVATE",
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 1)
        );

        InvalidRoadTypeException ex = assertThrows(InvalidRoadTypeException.class,
                () -> calculationService.calculateFee(input));

        assertEquals("INVALID_ROAD_TYPE", ex.getCode());
    }

    @Test
    void testGovernmentAgencyApplicant() {
        CalculationInputDTO input = new CalculationInputDTO(
                "dehradun",
                "CC",
                new BigDecimal("10.0"),
                new BigDecimal("2.0"),
                10,
                "GOVERNMENT_AGENCY",
                LocalDate.of(2026, 3, 2),
                LocalDate.of(2026, 3, 1)
        );

        CalculationResultDTO result = calculationService.calculateFee(input);

        assertEquals(20L, result.getAreaInSqm());
        // CC restorationRate = 2100 -> 20 * 2100 = 42,000
        assertEquals(new BigDecimal("42000.00"), result.getRestorationCharge());
        assertEquals(new BigDecimal("0.00"), result.getPermissionFee(), "Govt agency pays 0 permission fee");
        assertEquals(new BigDecimal("0.00"), result.getUrgencySurcharge(), "Govt agency pays 0 surcharge");
        assertEquals(new BigDecimal("10500.00"), result.getSecurityDeposit());
        assertEquals(new BigDecimal("52500.00"), result.getTotalAmount());
    }

    @Test
    void testUrgencyThresholdBoundary_StrictComparison() {
        CalculationInputDTO inputExact3Days = new CalculationInputDTO(
                "dehradun",
                "BT",
                new BigDecimal("10.0"),
                new BigDecimal("1.0"),
                2,
                "PRIVATE",
                LocalDate.of(2026, 3, 4),
                LocalDate.of(2026, 3, 1)
        );
        CalculationResultDTO result3Days = calculationService.calculateFee(inputExact3Days);
        assertEquals(new BigDecimal("0.00"), result3Days.getUrgencySurcharge(),
                "Strict comparison: exactly 3 days away must attract 0 surcharge");

        CalculationInputDTO input2Days = new CalculationInputDTO(
                "dehradun",
                "BT",
                new BigDecimal("10.0"),
                new BigDecimal("1.0"),
                2,
                "PRIVATE",
                LocalDate.of(2026, 3, 3),
                LocalDate.of(2026, 3, 1)
        );
        CalculationResultDTO result2Days = calculationService.calculateFee(input2Days);
        assertEquals(new BigDecimal("30.00"), result2Days.getUrgencySurcharge(),
                "2 days away (< 3) must attract 10% surcharge");
    }

    @Test
    void testAreaCeilRounding() {
        CalculationInputDTO input = new CalculationInputDTO(
                "dehradun",
                "WBM",
                new BigDecimal("12.1"),
                new BigDecimal("1.1"),
                1,
                "PRIVATE",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 1)
        );
        CalculationResultDTO result = calculationService.calculateFee(input);
        assertEquals(14L, result.getAreaInSqm(), "13.31 must round up to 14");
    }
}
