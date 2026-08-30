package com.muni.rcp.controller;

import com.muni.rcp.dto.CalculationRequestDTO;
import com.muni.rcp.dto.CalculationResponseDTO;
import com.muni.rcp.dto.CalculationResultDTO;
import com.muni.rcp.dto.ResponseInfo;
import com.muni.rcp.service.CalculationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rcp/v1")
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/_calculate")
    public ResponseEntity<CalculationResponseDTO> calculate(@Valid @RequestBody CalculationRequestDTO request) {
        CalculationResultDTO result = calculationService.calculateFee(request.getCalculation());

        String msgId = (request.getRequestInfo() != null) ? request.getRequestInfo().getMsgId() : null;
        ResponseInfo responseInfo = ResponseInfo.successful(msgId);

        return ResponseEntity.ok(new CalculationResponseDTO(responseInfo, result));
    }
}
