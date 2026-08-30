package com.muni.rcp;

import com.muni.rcp.entity.ApplicationSequenceEntity;
import com.muni.rcp.repository.ApplicationSequenceRepository;
import com.muni.rcp.service.SequenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SequenceServiceTest {

    private SequenceService sequenceService;
    private ApplicationSequenceRepository sequenceRepository;

    @BeforeEach
    void setUp() {
        sequenceRepository = Mockito.mock(ApplicationSequenceRepository.class);
        sequenceService = new SequenceService(sequenceRepository);
    }

    @Test
    void testFinancialYearCalculation() {
        assertEquals("2025-26", sequenceService.calculateFinancialYear(LocalDate.of(2026, 3, 31)));
        assertEquals("2025-26", sequenceService.calculateFinancialYear(LocalDate.of(2026, 1, 15)));

        assertEquals("2026-27", sequenceService.calculateFinancialYear(LocalDate.of(2026, 4, 1)));
        assertEquals("2026-27", sequenceService.calculateFinancialYear(LocalDate.of(2026, 8, 29)));
        assertEquals("2026-27", sequenceService.calculateFinancialYear(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void testTenantPrefixes() {
        assertEquals("DDN", sequenceService.getTenantPrefix("dehradun"));
        assertEquals("HDW", sequenceService.getTenantPrefix("haridwar"));
        assertEquals("ROO", sequenceService.getTenantPrefix("roorkee"));
    }

    @Test
    void testGenerateApplicationNumberFormat() {
        when(sequenceRepository.findByTenantIdAndFinancialYearForUpdate("dehradun", "2026-27"))
                .thenReturn(Optional.of(new ApplicationSequenceEntity("dehradun", "2026-27", 122L, "SYSTEM", System.currentTimeMillis())));

        String appNum = sequenceService.generateNextApplicationNumber("dehradun", LocalDate.of(2026, 8, 29), "test-user");

        assertEquals("DDN-RCP-000123-2026-27", appNum);
    }
}
