package com.muni.rcp.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RateConfigModel {

    @JsonProperty("defaults")
    private DefaultsConfig defaults;

    @JsonProperty("tenants")
    private Map<String, TenantConfig> tenants;

    public DefaultsConfig getDefaults() {
        return defaults;
    }

    public void setDefaults(DefaultsConfig defaults) {
        this.defaults = defaults;
    }

    public Map<String, TenantConfig> getTenants() {
        return tenants;
    }

    public void setTenants(Map<String, TenantConfig> tenants) {
        this.tenants = tenants;
    }

    public static class DefaultsConfig {
        @JsonProperty("roadTypes")
        private List<RoadTypeConfig> roadTypes;

        @JsonProperty("urgencyThresholdDays")
        private Integer urgencyThresholdDays;

        @JsonProperty("urgencySurchargePercent")
        private BigDecimal urgencySurchargePercent;

        @JsonProperty("securityDepositPercent")
        private BigDecimal securityDepositPercent;

        public List<RoadTypeConfig> getRoadTypes() {
            return roadTypes;
        }

        public void setRoadTypes(List<RoadTypeConfig> roadTypes) {
            this.roadTypes = roadTypes;
        }

        public Integer getUrgencyThresholdDays() {
            return urgencyThresholdDays;
        }

        public void setUrgencyThresholdDays(Integer urgencyThresholdDays) {
            this.urgencyThresholdDays = urgencyThresholdDays;
        }

        public BigDecimal getUrgencySurchargePercent() {
            return urgencySurchargePercent;
        }

        public void setUrgencySurchargePercent(BigDecimal urgencySurchargePercent) {
            this.urgencySurchargePercent = urgencySurchargePercent;
        }

        public BigDecimal getSecurityDepositPercent() {
            return securityDepositPercent;
        }

        public void setSecurityDepositPercent(BigDecimal securityDepositPercent) {
            this.securityDepositPercent = securityDepositPercent;
        }
    }

    public static class TenantConfig {
        @JsonProperty("roadTypes")
        private List<RoadTypeConfig> roadTypes;

        @JsonProperty("urgencyThresholdDays")
        private Integer urgencyThresholdDays;

        @JsonProperty("urgencySurchargePercent")
        private BigDecimal urgencySurchargePercent;

        @JsonProperty("securityDepositPercent")
        private BigDecimal securityDepositPercent;

        public List<RoadTypeConfig> getRoadTypes() {
            return roadTypes;
        }

        public void setRoadTypes(List<RoadTypeConfig> roadTypes) {
            this.roadTypes = roadTypes;
        }

        public Integer getUrgencyThresholdDays() {
            return urgencyThresholdDays;
        }

        public void setUrgencyThresholdDays(Integer urgencyThresholdDays) {
            this.urgencyThresholdDays = urgencyThresholdDays;
        }

        public BigDecimal getUrgencySurchargePercent() {
            return urgencySurchargePercent;
        }

        public void setUrgencySurchargePercent(BigDecimal urgencySurchargePercent) {
            this.urgencySurchargePercent = urgencySurchargePercent;
        }

        public BigDecimal getSecurityDepositPercent() {
            return securityDepositPercent;
        }

        public void setSecurityDepositPercent(BigDecimal securityDepositPercent) {
            this.securityDepositPercent = securityDepositPercent;
        }
    }

    public static class RoadTypeConfig {
        @JsonProperty("code")
        private String code;

        @JsonProperty("name")
        private String name;

        @JsonProperty("restorationRatePerSqm")
        private BigDecimal restorationRatePerSqm;

        @JsonProperty("permissionRatePerSqmPerDay")
        private BigDecimal permissionRatePerSqmPerDay;

        @JsonProperty("minSecurityDeposit")
        private BigDecimal minSecurityDeposit;

        @JsonProperty("active")
        private Boolean active;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getRestorationRatePerSqm() {
            return restorationRatePerSqm;
        }

        public void setRestorationRatePerSqm(BigDecimal restorationRatePerSqm) {
            this.restorationRatePerSqm = restorationRatePerSqm;
        }

        public BigDecimal getPermissionRatePerSqmPerDay() {
            return permissionRatePerSqmPerDay;
        }

        public void setPermissionRatePerSqmPerDay(BigDecimal permissionRatePerSqmPerDay) {
            this.permissionRatePerSqmPerDay = permissionRatePerSqmPerDay;
        }

        public BigDecimal getMinSecurityDeposit() {
            return minSecurityDeposit;
        }

        public void setMinSecurityDeposit(BigDecimal minSecurityDeposit) {
            this.minSecurityDeposit = minSecurityDeposit;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
