package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponseDTO {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Application")
    private ApplicationDetailDTO application;

    @JsonProperty("Applications")
    private List<ApplicationDetailDTO> applications;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("Errors")
    private List<ErrorDTO> errors;

    public ApplicationResponseDTO() {}

    public ApplicationResponseDTO(ResponseInfo responseInfo, ApplicationDetailDTO application) {
        this.responseInfo = responseInfo;
        this.application = application;
    }

    public ApplicationResponseDTO(ResponseInfo responseInfo, List<ApplicationDetailDTO> applications, Integer totalCount) {
        this.responseInfo = responseInfo;
        this.applications = applications;
        this.totalCount = totalCount;
    }

    public ApplicationResponseDTO(ResponseInfo responseInfo, List<ErrorDTO> errors) {
        this.responseInfo = responseInfo;
        this.errors = errors;
    }

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public ApplicationDetailDTO getApplication() {
        return application;
    }

    public void setApplication(ApplicationDetailDTO application) {
        this.application = application;
    }

    public List<ApplicationDetailDTO> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationDetailDTO> applications) {
        this.applications = applications;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<ErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDTO> errors) {
        this.errors = errors;
    }
}
