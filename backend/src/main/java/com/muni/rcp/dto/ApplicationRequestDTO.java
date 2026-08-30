package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ApplicationRequestDTO {

    @NotNull(message = "RequestInfo is required")
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull(message = "Application is required")
    @Valid
    @JsonProperty("Application")
    private ApplicationCreateDTO application;

    public ApplicationRequestDTO() {}

    public ApplicationRequestDTO(RequestInfo requestInfo, ApplicationCreateDTO application) {
        this.requestInfo = requestInfo;
        this.application = application;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public ApplicationCreateDTO getApplication() {
        return application;
    }

    public void setApplication(ApplicationCreateDTO application) {
        this.application = application;
    }
}
