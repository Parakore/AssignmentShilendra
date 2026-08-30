package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDetailDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("applicantName")
    private String applicantName;

    @JsonProperty("applicantMobile")
    private String applicantMobile;

    @JsonProperty("applicantEmail")
    private String applicantEmail;

    @JsonProperty("applicantType")
    private String applicantType;

    @JsonProperty("roadType")
    private String roadType;

    @JsonProperty("roadTypeName")
    private String roadTypeName;

    @JsonProperty("lengthInMeters")
    private BigDecimal lengthInMeters;

    @JsonProperty("widthInMeters")
    private BigDecimal widthInMeters;

    @JsonProperty("areaInSqm")
    private Long areaInSqm;

    @JsonProperty("durationInDays")
    private Integer durationInDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("proposedStartDate")
    private LocalDate proposedStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("applicationDate")
    private LocalDate applicationDate;

    @JsonProperty("location")
    private String location;

    @JsonProperty("description")
    private String description;

    @JsonProperty("calculation")
    private CalculationResultDTO calculation;

    @JsonProperty("timeline")
    private List<ActionHistoryDTO> timeline;

    @JsonProperty("allowedActions")
    private List<String> allowedActions;

    @JsonProperty("createdTime")
    private Long createdTime;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;

    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    public ApplicationDetailDTO() {}

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

    public String getRoadTypeName() {
        return roadTypeName;
    }

    public void setRoadTypeName(String roadTypeName) {
        this.roadTypeName = roadTypeName;
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

    public CalculationResultDTO getCalculation() {
        return calculation;
    }

    public void setCalculation(CalculationResultDTO calculation) {
        this.calculation = calculation;
    }

    public List<ActionHistoryDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<ActionHistoryDTO> timeline) {
        this.timeline = timeline;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
