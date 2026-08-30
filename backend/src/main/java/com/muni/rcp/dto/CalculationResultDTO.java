package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationResultDTO {

    @JsonProperty("areaInSqm")
    private Long areaInSqm;

    @JsonProperty("restorationCharge")
    private BigDecimal restorationCharge;

    @JsonProperty("permissionFee")
    private BigDecimal permissionFee;

    @JsonProperty("urgencySurcharge")
    private BigDecimal urgencySurcharge;

    @JsonProperty("securityDeposit")
    private BigDecimal securityDeposit;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("reviewRef")
    private String reviewRef;

    @JsonProperty("breakdownDetails")
    private Map<String, Object> breakdownDetails;

    public CalculationResultDTO() {}

    public CalculationResultDTO(Long areaInSqm, BigDecimal restorationCharge, BigDecimal permissionFee,
                                BigDecimal urgencySurcharge, BigDecimal securityDeposit, BigDecimal totalAmount,
                                String reviewRef) {
        this.areaInSqm = areaInSqm;
        this.restorationCharge = restorationCharge;
        this.permissionFee = permissionFee;
        this.urgencySurcharge = urgencySurcharge;
        this.securityDeposit = securityDeposit;
        this.totalAmount = totalAmount;
        this.reviewRef = reviewRef;
    }

    public Long getAreaInSqm() {
        return areaInSqm;
    }

    public void setAreaInSqm(Long areaInSqm) {
        this.areaInSqm = areaInSqm;
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

    public String getReviewRef() {
        return reviewRef;
    }

    public void setReviewRef(String reviewRef) {
        this.reviewRef = reviewRef;
    }

    public Map<String, Object> getBreakdownDetails() {
        return breakdownDetails;
    }

    public void setBreakdownDetails(Map<String, Object> breakdownDetails) {
        this.breakdownDetails = breakdownDetails;
    }
}
