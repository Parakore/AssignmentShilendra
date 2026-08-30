package com.muni.rcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muni.rcp.config.JsonWorkflowProvider;
import com.muni.rcp.dto.RequestInfo;
import com.muni.rcp.entity.ApplicationEntity;
import com.muni.rcp.exception.InvalidTransitionException;
import com.muni.rcp.exception.UnauthorizedRoleException;
import com.muni.rcp.repository.ActionHistoryRepository;
import com.muni.rcp.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class WorkflowServiceTest {

    private WorkflowService workflowService;
    private ActionHistoryRepository historyRepository;

    @BeforeEach
    void setUp() {
        JsonWorkflowProvider workflowProvider = new JsonWorkflowProvider(
                new DefaultResourceLoader(),
                new ObjectMapper(),
                "classpath:workflow-config.json"
        );
        workflowProvider.loadConfiguration();
        historyRepository = Mockito.mock(ActionHistoryRepository.class);
        workflowService = new WorkflowService(workflowProvider, historyRepository);
    }

    private RequestInfo createRequest(String role) {
        RequestInfo req = new RequestInfo();
        RequestInfo.UserInfo user = new RequestInfo.UserInfo("user-1", "9990000001", "dehradun",
                List.of(new RequestInfo.RoleInfo(role)));
        req.setUserInfo(user);
        return req;
    }

    private ApplicationEntity createSampleApplication(String status) {
        ApplicationEntity app = new ApplicationEntity();
        app.setId("app-123");
        app.setApplicationNumber("DDN-RCP-000001-2026-27");
        app.setTenantId("dehradun");
        app.setStatus(status);
        return app;
    }

    @Test
    void testVerifier_Verify_Success() {
        ApplicationEntity app = createSampleApplication("APPLIED");
        RequestInfo verifierReq = createRequest("VERIFIER");

        String nextStatus = workflowService.validateAndExecuteTransition(app, "VERIFY", "Documents verified", verifierReq);

        assertEquals("PENDING_APPROVAL", nextStatus);
        verify(historyRepository).save(any());
    }

    @Test
    void testVerifier_SendBack_Success() {
        ApplicationEntity app = createSampleApplication("PENDING_APPROVAL");
        RequestInfo verifierReq = createRequest("VERIFIER");

        String nextStatus = workflowService.validateAndExecuteTransition(app, "SEND_BACK", "Need revised road cut dimensions", verifierReq);

        assertEquals("APPLIED", nextStatus);
        verify(historyRepository).save(any());
    }

    @Test
    void testApprover_Approve_Success() {
        ApplicationEntity app = createSampleApplication("PENDING_APPROVAL");
        RequestInfo approverReq = createRequest("APPROVER");

        String nextStatus = workflowService.validateAndExecuteTransition(app, "APPROVE", "Permission granted", approverReq);

        assertEquals("APPROVED", nextStatus);
        verify(historyRepository).save(any());
    }

    @Test
    void testApprover_Reject_Success() {
        ApplicationEntity app = createSampleApplication("PENDING_APPROVAL");
        RequestInfo approverReq = createRequest("APPROVER");

        String nextStatus = workflowService.validateAndExecuteTransition(app, "REJECT", "Overlaps with upcoming road relaying", approverReq);

        assertEquals("REJECTED", nextStatus);
        verify(historyRepository).save(any());
    }

    @Test
    void testApplicant_Cancel_Success() {
        ApplicationEntity app = createSampleApplication("APPLIED");
        RequestInfo applicantReq = createRequest("APPLICANT");

        String nextStatus = workflowService.validateAndExecuteTransition(app, "CANCEL", "Work deferred", applicantReq);

        assertEquals("CANCELLED", nextStatus);
        verify(historyRepository).save(any());
    }

    @Test
    void testApplicant_Approve_ThrowsUnauthorizedRole() {
        ApplicationEntity app = createSampleApplication("PENDING_APPROVAL");
        RequestInfo applicantReq = createRequest("APPLICANT");

        UnauthorizedRoleException ex = assertThrows(UnauthorizedRoleException.class,
                () -> workflowService.validateAndExecuteTransition(app, "APPROVE", "Self approval attempt", applicantReq));

        assertEquals("UNAUTHORIZED_ROLE", ex.getCode());
    }

    @Test
    void testApprover_ApproveFromApplied_ThrowsInvalidTransition() {
        ApplicationEntity app = createSampleApplication("APPLIED");
        RequestInfo approverReq = createRequest("APPROVER");

        InvalidTransitionException ex = assertThrows(InvalidTransitionException.class,
                () -> workflowService.validateAndExecuteTransition(app, "APPROVE", "Premature approval", approverReq));

        assertEquals("INVALID_TRANSITION", ex.getCode());
    }

    @Test
    void testApplicant_CancelFromApproved_ThrowsInvalidTransition() {
        ApplicationEntity app = createSampleApplication("APPROVED");
        RequestInfo applicantReq = createRequest("APPLICANT");

        InvalidTransitionException ex = assertThrows(InvalidTransitionException.class,
                () -> workflowService.validateAndExecuteTransition(app, "CANCEL", "Cancel after approval", applicantReq));

        assertEquals("INVALID_TRANSITION", ex.getCode());
    }
}
