package com.muni.rcp.controller;

import com.muni.rcp.dto.ApplicationActionRequestDTO;
import com.muni.rcp.dto.ApplicationDetailDTO;
import com.muni.rcp.dto.ApplicationResponseDTO;
import com.muni.rcp.dto.ResponseInfo;
import com.muni.rcp.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rcp/v1")
public class WorkflowController {

    private final ApplicationService applicationService;

    public WorkflowController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/_action")
    public ResponseEntity<ApplicationResponseDTO> applyAction(@Valid @RequestBody ApplicationActionRequestDTO request) {
        ApplicationDetailDTO detail = applicationService.applyAction(
                request.getAction(),
                request.getApplication(),
                request.getRequestInfo()
        );

        String msgId = (request.getRequestInfo() != null) ? request.getRequestInfo().getMsgId() : null;
        ResponseInfo responseInfo = ResponseInfo.successful(msgId);

        return ResponseEntity.ok(new ApplicationResponseDTO(responseInfo, detail));
    }
}
