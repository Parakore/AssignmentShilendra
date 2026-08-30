package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ApplicationActionRequestDTO {

    @NotNull(message = "RequestInfo is required")
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull(message = "Action is required")
    @Valid
    @JsonProperty("Action")
    private ApplicationActionDTO action;

    @JsonProperty("Application")
    private ApplicationCreateDTO application;

    public ApplicationActionRequestDTO() {}

    public ApplicationActionRequestDTO(RequestInfo requestInfo, ApplicationActionDTO action) {
        this.requestInfo = requestInfo;
        this.action = action;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public ApplicationActionDTO getAction() {
        return action;
    }

    public void setAction(ApplicationActionDTO action) {
        this.action = action;
    }

    public ApplicationCreateDTO getApplication() {
        return application;
    }

    public void setApplication(ApplicationCreateDTO application) {
        this.application = application;
    }
}
