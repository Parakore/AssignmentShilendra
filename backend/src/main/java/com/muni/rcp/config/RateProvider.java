package com.muni.rcp.config;

import java.math.BigDecimal;
import java.util.Map;

public interface RateProvider {

    ResolvedTenantRates getRatesForTenant(String tenantId);

    ResolvedRoadTypeRate getRoadTypeRate(String tenantId, String roadTypeCode);

    record ResolvedRoadTypeRate(
            String code,
            String name,
            BigDecimal restorationRatePerSqm,
            BigDecimal permissionRatePerSqmPerDay,
            BigDecimal minSecurityDeposit,
            boolean active
    ) {}

    record ResolvedTenantRates(
            String tenantId,
            int urgencyThresholdDays,
            BigDecimal urgencySurchargePercent,
            BigDecimal securityDepositPercent,
            Map<String, ResolvedRoadTypeRate> roadTypes
    ) {}
}
