package com.muni.rcp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rcp_application")
public class ApplicationEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "application_number", length = 64, nullable = false, unique = true)
    private String applicationNumber;

    @Column(name = "tenant_id", length = 64, nullable = false)
    private String tenantId;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "applicant_name", length = 128, nullable = false)
    private String applicantName;

    @Column(name = "applicant_mobile", length = 20, nullable = false)
    private String applicantMobile;

    @Column(name = "applicant_email", length = 128)
    private String applicantEmail;

    @Column(name = "applicant_type", length = 32, nullable = false)
    private String applicantType;

    @Column(name = "road_type", length = 32, nullable = false)
    private String roadType;

    @Column(name = "length_in_meters", precision = 10, scale = 2, nullable = false)
    private BigDecimal lengthInMeters;

    @Column(name = "width_in_meters", precision = 10, scale = 2, nullable = false)
    private BigDecimal widthInMeters;

    @Column(name = "area_in_sqm", nullable = false)
    private Long areaInSqm;

    @Column(name = "duration_in_days", nullable = false)
    private Integer durationInDays;

    @Column(name = "proposed_start_date", nullable = false)
    private LocalDate proposedStartDate;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "location", length = 255, nullable = false)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "restoration_charge", precision = 14, scale = 2, nullable = false)
    private BigDecimal restorationCharge;

    @Column(name = "permission_fee", precision = 14, scale = 2, nullable = false)
    private BigDecimal permissionFee;

    @Column(name = "urgency_surcharge", precision = 14, scale = 2, nullable = false)
    private BigDecimal urgencySurcharge;

    @Column(name = "security_deposit", precision = 14, scale = 2, nullable = false)
    private BigDecimal securityDeposit;

    @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @Column(name = "created_time", nullable = false)
    private Long createdTime;

    @Column(name = "last_modified_by", length = 64, nullable = false)
    private String lastModifiedBy;

    @Column(name = "last_modified_time", nullable = false)
    private Long lastModifiedTime;

    public ApplicationEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantMobile() {
        return applicantMobile;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public BigDecimal getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(BigDecimal lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public BigDecimal getWidthInMeters() {
        return widthInMeters;
    }

    public void setWidthInMeters(BigDecimal widthInMeters) {
        this.widthInMeters = widthInMeters;
    }

    public Long getAreaInSqm() {
        return areaInSqm;
    }

    public void setAreaInSqm(Long areaInSqm) {
        this.areaInSqm = areaInSqm;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public LocalDate getProposedStartDate() {
        return proposedStartDate;
    }

    public void setProposedStartDate(LocalDate proposedStartDate) {
        this.proposedStartDate = proposedStartDate;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRestorationCharge() {
        return restorationCharge;
    }

    public void setRestorationCharge(BigDecimal restorationCharge) {
        this.restorationCharge = restorationCharge;
    }

    public BigDecimal getPermissionFee() {
        return permissionFee;
    }

    public void setPermissionFee(BigDecimal permissionFee) {
        this.permissionFee = permissionFee;
    }

    public BigDecimal getUrgencySurcharge() {
        return urgencySurcharge;
    }

    public void setUrgencySurcharge(BigDecimal urgencySurcharge) {
        this.urgencySurcharge = urgencySurcharge;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
