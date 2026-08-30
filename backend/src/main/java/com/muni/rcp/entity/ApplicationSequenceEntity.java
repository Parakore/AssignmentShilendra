package com.muni.rcp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rcp_application_sequence")
@IdClass(ApplicationSequenceId.class)
public class ApplicationSequenceEntity {

    @Id
    @Column(name = "tenant_id", length = 64, nullable = false)
    private String tenantId;

    @Id
    @Column(name = "financial_year", length = 16, nullable = false)
    private String financialYear;

    @Column(name = "last_sequence", nullable = false)
    private Long lastSequence;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @Column(name = "created_time", nullable = false)
    private Long createdTime;

    @Column(name = "last_modified_by", length = 64, nullable = false)
    private String lastModifiedBy;

    @Column(name = "last_modified_time", nullable = false)
    private Long lastModifiedTime;

    public ApplicationSequenceEntity() {}

    public ApplicationSequenceEntity(String tenantId, String financialYear, Long lastSequence, String createdBy, Long createdTime) {
        this.tenantId = tenantId;
        this.financialYear = financialYear;
        this.lastSequence = lastSequence;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.lastModifiedBy = createdBy;
        this.lastModifiedTime = createdTime;
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

    public Long getLastSequence() {
        return lastSequence;
    }

    public void setLastSequence(Long lastSequence) {
        this.lastSequence = lastSequence;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
