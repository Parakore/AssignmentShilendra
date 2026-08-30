package com.muni.rcp.service;

import com.muni.rcp.config.WorkflowConfigModel;
import com.muni.rcp.config.WorkflowProvider;
import com.muni.rcp.dto.ActionHistoryDTO;
import com.muni.rcp.dto.RequestInfo;
import com.muni.rcp.entity.ActionHistoryEntity;
import com.muni.rcp.entity.ApplicationEntity;
import com.muni.rcp.exception.InvalidTransitionException;
import com.muni.rcp.exception.UnauthorizedRoleException;
import com.muni.rcp.repository.ActionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowService {

    private final WorkflowProvider workflowProvider;
    private final ActionHistoryRepository historyRepository;

    public WorkflowService(WorkflowProvider workflowProvider, ActionHistoryRepository historyRepository) {
        this.workflowProvider = workflowProvider;
        this.historyRepository = historyRepository;
    }

    public List<String> getAllowedNextActions(String currentStatus, List<String> userRoles) {
        return workflowProvider.getAvailableTransitions(currentStatus, userRoles).stream()
                .map(WorkflowConfigModel.TransitionConfig::getAction)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public String validateAndExecuteTransition(
            ApplicationEntity application,
            String action,
            String comment,
            RequestInfo requestInfo) {

        String currentStatus = application.getStatus();
        String upperAction = action.toUpperCase().trim();

        List<String> userRoles = extractRoles(requestInfo);
        String actorUuid = extractActorUuid(requestInfo);
        String actorName = extractActorName(requestInfo);
        String primaryRole = userRoles.isEmpty() ? "APPLICANT" : userRoles.get(0);

        WorkflowConfigModel.TransitionConfig transition = workflowProvider.getTransition(currentStatus, upperAction);
        if (transition == null) {
            throw new InvalidTransitionException(
                    String.format("Action '%s' is not permitted for application in '%s' state", upperAction, currentStatus)
            );
        }

        boolean roleAuthorized = transition.getAllowedRoles().stream()
                .anyMatch(reqRole -> userRoles.stream().anyMatch(uRole -> uRole.equalsIgnoreCase(reqRole)));

        if (!roleAuthorized) {
            throw new UnauthorizedRoleException(
                    String.format("Role(s) %s are not authorized to perform action '%s'. Required: %s",
                            userRoles, upperAction, transition.getAllowedRoles())
            );
        }

        String nextStatus = transition.getToState();
        long now = System.currentTimeMillis();

        ActionHistoryEntity history = new ActionHistoryEntity(
                UUID.randomUUID().toString(),
                application.getId(),
                application.getApplicationNumber(),
                application.getTenantId(),
                upperAction,
                currentStatus,
                nextStatus,
                actorUuid,
                actorName,
                primaryRole,
                comment,
                actorUuid,
                now
        );
        historyRepository.save(history);

        return nextStatus;
    }

    @Transactional
    public void recordInitialHistory(ApplicationEntity application, RequestInfo requestInfo) {
        long now = System.currentTimeMillis();
        String actorUuid = extractActorUuid(requestInfo);
        String actorName = extractActorName(requestInfo);
        List<String> roles = extractRoles(requestInfo);
        String role = roles.isEmpty() ? "APPLICANT" : roles.get(0);

        ActionHistoryEntity history = new ActionHistoryEntity(
                UUID.randomUUID().toString(),
                application.getId(),
                application.getApplicationNumber(),
                application.getTenantId(),
                "CREATE",
                null,
                application.getStatus(),
                actorUuid,
                actorName,
                role,
                "Application created and submitted",
                actorUuid,
                now
        );
        historyRepository.save(history);
    }

    public List<ActionHistoryDTO> getTimeline(String tenantId, String applicationId) {
        List<ActionHistoryEntity> entities = historyRepository
                .findByTenantIdAndApplicationIdOrderByCreatedTimeAsc(tenantId, applicationId);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActionHistoryDTO toDTO(ActionHistoryEntity entity) {
        return new ActionHistoryDTO(
                entity.getId(),
                entity.getAction(),
                entity.getFromStatus(),
                entity.getToStatus(),
                entity.getActorUuid(),
                entity.getActorName(),
                entity.getActorRole(),
                entity.getComment(),
                entity.getCreatedTime()
        );
    }

    public List<String> extractRoles(RequestInfo requestInfo) {
        if (requestInfo == null || requestInfo.getUserInfo() == null || requestInfo.getUserInfo().getRoles() == null) {
            return Collections.singletonList("APPLICANT");
        }
        return requestInfo.getUserInfo().getRoles().stream()
                .map(RequestInfo.RoleInfo::getCode)
                .filter(c -> c != null && !c.isBlank())
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    public String extractActorUuid(RequestInfo requestInfo) {
        if (requestInfo != null && requestInfo.getUserInfo() != null && requestInfo.getUserInfo().getUuid() != null) {
            return requestInfo.getUserInfo().getUuid();
        }
        return "anonymous-user";
    }

    public String extractActorName(RequestInfo requestInfo) {
        if (requestInfo != null && requestInfo.getUserInfo() != null) {
            if (requestInfo.getUserInfo().getName() != null) {
                return requestInfo.getUserInfo().getName();
            }
            if (requestInfo.getUserInfo().getUserName() != null) {
                return requestInfo.getUserInfo().getUserName();
            }
        }
        return "Anonymous User";
    }
}
