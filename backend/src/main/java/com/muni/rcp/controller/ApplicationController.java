package com.muni.rcp.controller;

import com.muni.rcp.dto.*;
import com.muni.rcp.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rcp/v1")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/_create")
    public ResponseEntity<ApplicationResponseDTO> createApplication(@Valid @RequestBody ApplicationRequestDTO request) {
        ApplicationDetailDTO detail = applicationService.createApplication(request.getApplication(), request.getRequestInfo());

        String msgId = (request.getRequestInfo() != null) ? request.getRequestInfo().getMsgId() : null;
        ResponseInfo responseInfo = ResponseInfo.successful(msgId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApplicationResponseDTO(responseInfo, detail));
    }

    @PostMapping("/_search")
    public ResponseEntity<ApplicationResponseDTO> searchApplications(@Valid @RequestBody SearchRequestDTO request) {
        List<ApplicationDetailDTO> results = applicationService.searchApplications(request.getSearchCriteria(), request.getRequestInfo());

        String msgId = (request.getRequestInfo() != null) ? request.getRequestInfo().getMsgId() : null;
        ResponseInfo responseInfo = ResponseInfo.successful(msgId);

        return ResponseEntity.ok(new ApplicationResponseDTO(responseInfo, results, results.size()));
    }
}
