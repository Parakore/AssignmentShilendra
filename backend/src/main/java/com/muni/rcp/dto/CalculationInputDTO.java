package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CalculationInputDTO {

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("applicationDate")
    private LocalDate applicationDate;

    public CalculationInputDTO() {}

    public CalculationInputDTO(String tenantId, String roadType, BigDecimal lengthInMeters, BigDecimal widthInMeters,
                               Integer durationInDays, String applicantType, LocalDate proposedStartDate, LocalDate applicationDate) {
        this.tenantId = tenantId;
        this.roadType = roadType;
        this.lengthInMeters = lengthInMeters;
        this.widthInMeters = widthInMeters;
        this.durationInDays = durationInDays;
        this.applicantType = applicantType;
        this.proposedStartDate = proposedStartDate;
        this.applicationDate = applicationDate;
    }

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

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
}
