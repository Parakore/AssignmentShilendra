package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchCriteriaDTO {

    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    public SearchCriteriaDTO() {}

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getOffset() {
        return offset != null && offset >= 0 ? offset : 0;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
