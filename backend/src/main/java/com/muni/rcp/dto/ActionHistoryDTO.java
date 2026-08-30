package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionHistoryDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("action")
    private String action;

    @JsonProperty("fromStatus")
    private String fromStatus;

    @JsonProperty("toStatus")
    private String toStatus;

    @JsonProperty("actorUuid")
    private String actorUuid;

    @JsonProperty("actorName")
    private String actorName;

    @JsonProperty("actorRole")
    private String actorRole;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("createdTime")
    private Long createdTime;

    public ActionHistoryDTO() {}

    public ActionHistoryDTO(String id, String action, String fromStatus, String toStatus,
                            String actorUuid, String actorName, String actorRole, String comment, Long createdTime) {
        this.id = id;
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actorUuid = actorUuid;
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.comment = comment;
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getActorUuid() {
        return actorUuid;
    }

    public void setActorUuid(String actorUuid) {
        this.actorUuid = actorUuid;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }
}
