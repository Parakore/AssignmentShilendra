package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ApplicationCreateDTO {

    @NotBlank(message = "tenantId is required")
    @JsonProperty("tenantId")
    private String tenantId;

    @NotBlank(message = "roadType is required")
    @JsonProperty("roadType")
    private String roadType;

    @NotNull(message = "lengthInMeters is required")
    @DecimalMin(value = "0.01", message = "lengthInMeters must be greater than 0")
    @JsonProperty("lengthInMeters")
    private BigDecimal lengthInMeters;

    @NotNull(message = "widthInMeters is required")
    @DecimalMin(value = "0.01", message = "widthInMeters must be greater than 0")
    @JsonProperty("widthInMeters")
    private BigDecimal widthInMeters;

    @NotNull(message = "durationInDays is required")
    @Min(value = 1, message = "durationInDays must be at least 1")
    @JsonProperty("durationInDays")
    private Integer durationInDays;

    @NotBlank(message = "applicantType is required")
    @JsonProperty("applicantType")
    private String applicantType;

    @NotNull(message = "proposedStartDate is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("proposedStartDate")
    private LocalDate proposedStartDate;

    @NotBlank(message = "applicantName is required")
    @JsonProperty("applicantName")
    private String applicantName;

    @NotBlank(message = "applicantMobile is required")
    @JsonProperty("applicantMobile")
    private String applicantMobile;

    @JsonProperty("applicantEmail")
    private String applicantEmail;

    @NotBlank(message = "location is required")
    @JsonProperty("location")
    private String location;

    @JsonProperty("description")
    private String description;

    public ApplicationCreateDTO() {}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public LocalDate getProposedStartDate() {
        return proposedStartDate;
    }

    public void setProposedStartDate(LocalDate proposedStartDate) {
        this.proposedStartDate = proposedStartDate;
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

    public void setEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
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
}
