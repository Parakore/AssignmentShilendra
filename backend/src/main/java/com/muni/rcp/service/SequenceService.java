package com.muni.rcp.service;

import com.muni.rcp.entity.ApplicationSequenceEntity;
import com.muni.rcp.repository.ApplicationSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class SequenceService {

    private final ApplicationSequenceRepository sequenceRepository;

    private static final Map<String, String> TENANT_PREFIXES = Map.of(
            "dehradun", "DDN",
            "haridwar", "HDW"
    );

    public SequenceService(ApplicationSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public String generateNextApplicationNumber(String tenantId, LocalDate date, String createdBy) {
        String tenant = (tenantId != null ? tenantId.toLowerCase().trim() : "default");
        String financialYear = calculateFinancialYear(date != null ? date : LocalDate.now());
        String prefix = getTenantPrefix(tenant);

        long sequenceNumber = getNextSequenceNumber(tenant, financialYear, createdBy);

        return String.format("%s-RCP-%06d-%s", prefix, sequenceNumber, financialYear);
    }

    private synchronized long getNextSequenceNumber(String tenant, String financialYear, String createdBy) {
        Optional<ApplicationSequenceEntity> optionalSeq = sequenceRepository
                .findByTenantIdAndFinancialYearForUpdate(tenant, financialYear);

        long nextVal;
        long now = System.currentTimeMillis();

        if (optionalSeq.isPresent()) {
            ApplicationSequenceEntity seq = optionalSeq.get();
            nextVal = seq.getLastSequence() + 1;
            seq.setLastSequence(nextVal);
            seq.setLastModifiedBy(createdBy != null ? createdBy : "SYSTEM");
            seq.setLastModifiedTime(now);
            sequenceRepository.save(seq);
        } else {
            nextVal = 1L;
            ApplicationSequenceEntity seq = new ApplicationSequenceEntity(
                    tenant,
                    financialYear,
                    nextVal,
                    createdBy != null ? createdBy : "SYSTEM",
                    now
            );
            sequenceRepository.save(seq);
        }

        return nextVal;
    }

    public String calculateFinancialYear(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int startYear;
        int endYear;

        if (month >= 4) {
            startYear = year;
            endYear = year + 1;
        } else {
            startYear = year - 1;
            endYear = year;
        }

        int endShort = endYear % 100;
        return String.format("%04d-%02d", startYear, endShort);
    }

    public String getTenantPrefix(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return "MUNI";
        }
        String lower = tenantId.toLowerCase().trim();
        if (TENANT_PREFIXES.containsKey(lower)) {
            return TENANT_PREFIXES.get(lower);
        }
        return lower.length() >= 3 ? lower.substring(0, 3).toUpperCase() : lower.toUpperCase();
    }
}
