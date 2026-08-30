package com.muni.rcp.entity;

import java.io.Serializable;
import java.util.Objects;

public class ApplicationSequenceId implements Serializable {

    private String tenantId;
    private String financialYear;

    public ApplicationSequenceId() {}

    public ApplicationSequenceId(String tenantId, String financialYear) {
        this.tenantId = tenantId;
        this.financialYear = financialYear;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationSequenceId that = (ApplicationSequenceId) o;
        return Objects.equals(tenantId, that.tenantId) && Objects.equals(financialYear, that.financialYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, financialYear);
    }
}
