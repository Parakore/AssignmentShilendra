package com.muni.rcp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rcp_action_history")
public class ActionHistoryEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "application_id", length = 64, nullable = false)
    private String applicationId;

    @Column(name = "application_number", length = 64, nullable = false)
    private String applicationNumber;

    @Column(name = "tenant_id", length = 64, nullable = false)
    private String tenantId;

    @Column(name = "action", length = 32, nullable = false)
    private String action;

    @Column(name = "from_status", length = 32)
    private String fromStatus;

    @Column(name = "to_status", length = 32, nullable = false)
    private String toStatus;

    @Column(name = "actor_uuid", length = 64, nullable = false)
    private String actorUuid;

    @Column(name = "actor_name", length = 128, nullable = false)
    private String actorName;

    @Column(name = "actor_role", length = 64, nullable = false)
    private String actorRole;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @Column(name = "created_time", nullable = false)
    private Long createdTime;

    @Column(name = "last_modified_by", length = 64, nullable = false)
    private String lastModifiedBy;

    @Column(name = "last_modified_time", nullable = false)
    private Long lastModifiedTime;

    public ActionHistoryEntity() {}

    public ActionHistoryEntity(String id, String applicationId, String applicationNumber, String tenantId,
                               String action, String fromStatus, String toStatus, String actorUuid,
                               String actorName, String actorRole, String comment,
                               String createdBy, Long createdTime) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationNumber = applicationNumber;
        this.tenantId = tenantId;
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actorUuid = actorUuid;
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.comment = comment;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.lastModifiedBy = createdBy;
        this.lastModifiedTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
