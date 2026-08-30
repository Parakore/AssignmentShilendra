package com.muni.rcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muni.rcp.exception.InvalidRoadTypeException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JsonRateProvider implements RateProvider {

    private static final Logger log = LoggerFactory.getLogger(JsonRateProvider.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final String configPath;

    private RateConfigModel rawConfig;
    private final Map<String, ResolvedTenantRates> resolvedTenantRatesCache = new ConcurrentHashMap<>();

    public JsonRateProvider(
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            @Value("${rcp.rates.config-path:classpath:rates-config.json}") String configPath) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.configPath = configPath;
    }

    @PostConstruct
    public synchronized void loadConfiguration() {
        try {
            log.info("Loading rate configuration from: {}", configPath);
            Resource resource = resourceLoader.getResource(configPath);
            try (InputStream is = resource.getInputStream()) {
                this.rawConfig = objectMapper.readValue(is, RateConfigModel.class);
            }
            resolvedTenantRatesCache.clear();
            log.info("Successfully loaded rate configuration with {} default road types",
                    rawConfig.getDefaults() != null && rawConfig.getDefaults().getRoadTypes() != null
                            ? rawConfig.getDefaults().getRoadTypes().size() : 0);
        } catch (Exception e) {
            log.error("Failed to load rate configuration from {}", configPath, e);
            throw new IllegalStateException("Could not load rate configuration from " + configPath, e);
        }
    }

    @Override
    public ResolvedTenantRates getRatesForTenant(String tenantId) {
        String effectiveTenant = (tenantId == null || tenantId.isBlank()) ? "default" : tenantId.toLowerCase().trim();
        return resolvedTenantRatesCache.computeIfAbsent(effectiveTenant, this::resolveTenantRates);
    }

    @Override
    public ResolvedRoadTypeRate getRoadTypeRate(String tenantId, String roadTypeCode) {
        if (roadTypeCode == null || roadTypeCode.isBlank()) {
            throw new InvalidRoadTypeException("Road type cannot be empty");
        }
        ResolvedTenantRates tenantRates = getRatesForTenant(tenantId);
        ResolvedRoadTypeRate rate = tenantRates.roadTypes().get(roadTypeCode.toUpperCase().trim());

        if (rate == null) {
            throw new InvalidRoadTypeException("Unknown road type '" + roadTypeCode + "' for tenant " + tenantId);
        }

        if (!rate.active()) {
            throw new InvalidRoadTypeException("Road type " + roadTypeCode.toUpperCase().trim() + " is not active for tenant " + tenantId);
        }

        return rate;
    }

    private ResolvedTenantRates resolveTenantRates(String tenantId) {
        RateConfigModel.DefaultsConfig defaults = rawConfig.getDefaults();
        if (defaults == null) {
            throw new IllegalStateException("Default rate configuration is missing");
        }

        RateConfigModel.TenantConfig tenantOverride = rawConfig.getTenants() != null
                ? rawConfig.getTenants().get(tenantId)
                : null;

        int urgencyThresholdDays = (tenantOverride != null && tenantOverride.getUrgencyThresholdDays() != null)
                ? tenantOverride.getUrgencyThresholdDays()
                : defaults.getUrgencyThresholdDays();

        BigDecimal urgencySurchargePercent = (tenantOverride != null && tenantOverride.getUrgencySurchargePercent() != null)
                ? tenantOverride.getUrgencySurchargePercent()
                : defaults.getUrgencySurchargePercent();

        BigDecimal securityDepositPercent = (tenantOverride != null && tenantOverride.getSecurityDepositPercent() != null)
                ? tenantOverride.getSecurityDepositPercent()
                : defaults.getSecurityDepositPercent();

        Map<String, ResolvedRoadTypeRate> resolvedMap = new HashMap<>();

        if (defaults.getRoadTypes() != null) {
            for (RateConfigModel.RoadTypeConfig defRt : defaults.getRoadTypes()) {
                if (defRt.getCode() != null) {
                    resolvedMap.put(defRt.getCode().toUpperCase().trim(), new ResolvedRoadTypeRate(
                            defRt.getCode().toUpperCase().trim(),
                            defRt.getName() != null ? defRt.getName() : defRt.getCode(),
                            defRt.getRestorationRatePerSqm(),
                            defRt.getPermissionRatePerSqmPerDay(),
                            defRt.getMinSecurityDeposit(),
                            defRt.getActive() != null ? defRt.getActive() : true
                    ));
                }
            }
        }

        if (tenantOverride != null && tenantOverride.getRoadTypes() != null) {
            for (RateConfigModel.RoadTypeConfig ovrRt : tenantOverride.getRoadTypes()) {
                if (ovrRt.getCode() != null) {
                    String code = ovrRt.getCode().toUpperCase().trim();
                    ResolvedRoadTypeRate existing = resolvedMap.get(code);

                    String name = (ovrRt.getName() != null)
                            ? ovrRt.getName()
                            : (existing != null ? existing.name() : code);

                    BigDecimal restorationRate = (ovrRt.getRestorationRatePerSqm() != null)
                            ? ovrRt.getRestorationRatePerSqm()
                            : (existing != null ? existing.restorationRatePerSqm() : BigDecimal.ZERO);

                    BigDecimal permissionRate = (ovrRt.getPermissionRatePerSqmPerDay() != null)
                            ? ovrRt.getPermissionRatePerSqmPerDay()
                            : (existing != null ? existing.permissionRatePerSqmPerDay() : BigDecimal.ZERO);

                    BigDecimal minDeposit = (ovrRt.getMinSecurityDeposit() != null)
                            ? ovrRt.getMinSecurityDeposit()
                            : (existing != null ? existing.minSecurityDeposit() : BigDecimal.ZERO);

                    boolean active = (ovrRt.getActive() != null)
                            ? ovrRt.getActive()
                            : (existing == null || existing.active());

                    resolvedMap.put(code, new ResolvedRoadTypeRate(
                            code, name, restorationRate, permissionRate, minDeposit, active
                    ));
                }
            }
        }

        return new ResolvedTenantRates(
                tenantId,
                urgencyThresholdDays,
                urgencySurchargePercent,
                securityDepositPercent,
                Collections.unmodifiableMap(resolvedMap)
        );
    }
}
