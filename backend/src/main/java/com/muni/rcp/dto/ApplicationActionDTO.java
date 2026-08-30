package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class ApplicationActionDTO {

    @NotBlank(message = "applicationNumber is required")
    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @NotBlank(message = "action is required")
    @JsonProperty("action")
    private String action;

    @JsonProperty("comment")
    private String comment;

    public ApplicationActionDTO() {}

    public ApplicationActionDTO(String applicationNumber, String action, String comment) {
        this.applicationNumber = applicationNumber;
        this.action = action;
        this.comment = comment;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
