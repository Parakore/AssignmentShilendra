package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CalculationRequestDTO {

    @NotNull(message = "RequestInfo is required")
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull(message = "Calculation is required")
    @Valid
    @JsonProperty("Calculation")
    private CalculationInputDTO calculation;

    public CalculationRequestDTO() {}

    public CalculationRequestDTO(RequestInfo requestInfo, CalculationInputDTO calculation) {
        this.requestInfo = requestInfo;
        this.calculation = calculation;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public CalculationInputDTO getCalculation() {
        return calculation;
    }

    public void setCalculation(CalculationInputDTO calculation) {
        this.calculation = calculation;
    }
}
