package com.muni.rcp.service;

import com.muni.rcp.config.RateProvider;
import com.muni.rcp.config.RateProvider.ResolvedRoadTypeRate;
import com.muni.rcp.config.RateProvider.ResolvedTenantRates;
import com.muni.rcp.dto.CalculationInputDTO;
import com.muni.rcp.dto.CalculationResultDTO;
import com.muni.rcp.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CalculationService {

    private final RateProvider rateProvider;
    private final String reviewRef;

    public CalculationService(
            RateProvider rateProvider,
            @Value("${rcp.review-ref:K7Q2}") String reviewRef) {
        this.rateProvider = rateProvider;
        this.reviewRef = reviewRef;
    }

    public CalculationResultDTO calculateFee(CalculationInputDTO input) {
        validateCalculationInput(input);

        String tenantId = input.getTenantId().toLowerCase().trim();
        String roadTypeCode = input.getRoadType().toUpperCase().trim();

        ResolvedTenantRates tenantRates = rateProvider.getRatesForTenant(tenantId);
        ResolvedRoadTypeRate roadTypeRate = rateProvider.getRoadTypeRate(tenantId, roadTypeCode);

        BigDecimal length = input.getLengthInMeters();
        BigDecimal width = input.getWidthInMeters();
        BigDecimal rawArea = length.multiply(width);
        long areaInSqm = (long) Math.ceil(rawArea.doubleValue());
        if (areaInSqm <= 0) {
            areaInSqm = 1L;
        }
        BigDecimal area = BigDecimal.valueOf(areaInSqm);

        BigDecimal restorationCharge = area.multiply(roadTypeRate.restorationRatePerSqm())
                .setScale(2, RoundingMode.HALF_UP);

        boolean isGovtAgency = "GOVERNMENT_AGENCY".equalsIgnoreCase(input.getApplicantType());
        BigDecimal permissionFee;
        if (isGovtAgency) {
            permissionFee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        } else {
            BigDecimal days = BigDecimal.valueOf(input.getDurationInDays());
            permissionFee = area.multiply(roadTypeRate.permissionRatePerSqmPerDay())
                    .multiply(days)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        LocalDate appDate = input.getApplicationDate() != null ? input.getApplicationDate() : LocalDate.now();
        LocalDate startDate = input.getProposedStartDate();
        long daysUntilStart = ChronoUnit.DAYS.between(appDate, startDate);

        BigDecimal urgencySurcharge = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        int urgencyThreshold = tenantRates.urgencyThresholdDays();
        boolean isUrgent = daysUntilStart < urgencyThreshold;

        if (isUrgent && permissionFee.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal surchargePercent = tenantRates.urgencySurchargePercent();
            urgencySurcharge = permissionFee.multiply(surchargePercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal depositPercent = tenantRates.securityDepositPercent();
        BigDecimal calculatedDeposit = restorationCharge.multiply(depositPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal minDeposit = roadTypeRate.minSecurityDeposit();
        BigDecimal securityDeposit = calculatedDeposit.max(minDeposit).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = restorationCharge
                .add(permissionFee)
                .add(urgencySurcharge)
                .add(securityDeposit)
                .setScale(2, RoundingMode.HALF_UP);

        CalculationResultDTO result = new CalculationResultDTO(
                areaInSqm,
                restorationCharge,
                permissionFee,
                urgencySurcharge,
                securityDeposit,
                totalAmount,
                reviewRef
        );

        Map<String, Object> breakdown = new LinkedHashMap<>();
        breakdown.put("rawProductArea", rawArea.stripTrailingZeros().toPlainString());
        breakdown.put("roundedAreaSqm", areaInSqm);
        breakdown.put("roadTypeName", roadTypeRate.name());
        breakdown.put("restorationRatePerSqm", roadTypeRate.restorationRatePerSqm());
        breakdown.put("permissionRatePerSqmPerDay", roadTypeRate.permissionRatePerSqmPerDay());
        breakdown.put("minSecurityDepositFloor", minDeposit);
        breakdown.put("securityDepositPercent", depositPercent);
        breakdown.put("calculatedDepositFromPercent", calculatedDeposit);
        breakdown.put("daysUntilStart", daysUntilStart);
        breakdown.put("urgencyThresholdDays", urgencyThreshold);
        breakdown.put("isUrgent", isUrgent);
        breakdown.put("urgencySurchargePercent", tenantRates.urgencySurchargePercent());
        breakdown.put("isGovtAgency", isGovtAgency);

        result.setBreakdownDetails(breakdown);
        return result;
    }
    

    private void validateCalculationInput(CalculationInputDTO input) {
        if (input == null) {
            throw new InvalidInputException("Calculation payload cannot be null");
        }
        if (input.getTenantId() == null || input.getTenantId().isBlank()) {
            throw new InvalidInputException("tenantId is required");
        }
        if (input.getRoadType() == null || input.getRoadType().isBlank()) {
            throw new InvalidInputException("roadType is required");
        }
        if (input.getLengthInMeters() == null || input.getLengthInMeters().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("lengthInMeters must be positive");
        }
        if (input.getWidthInMeters() == null || input.getWidthInMeters().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("widthInMeters must be positive");
        }
        if (input.getDurationInDays() == null || input.getDurationInDays() <= 0) {
            throw new InvalidInputException("durationInDays must be at least 1");
        }
        if (input.getDurationInDays() > 365) {
            throw new InvalidInputException("durationInDays cannot exceed 365 days");
        }
        if (input.getProposedStartDate() == null) {
            throw new InvalidInputException("proposedStartDate is required");
        }
        LocalDate appDate = input.getApplicationDate() != null ? input.getApplicationDate() : LocalDate.now();
        if (input.getProposedStartDate().isBefore(appDate)) {
            throw new InvalidInputException("proposedStartDate cannot be before application date (" + appDate + ")");
        }
    }
}
