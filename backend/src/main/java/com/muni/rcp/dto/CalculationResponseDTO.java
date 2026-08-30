package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationResponseDTO {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Calculation")
    private CalculationResultDTO calculation;

    @JsonProperty("Errors")
    private List<ErrorDTO> errors;

    public CalculationResponseDTO() {}

    public CalculationResponseDTO(ResponseInfo responseInfo, CalculationResultDTO calculation) {
        this.responseInfo = responseInfo;
        this.calculation = calculation;
    }

    public CalculationResponseDTO(ResponseInfo responseInfo, List<ErrorDTO> errors) {
        this.responseInfo = responseInfo;
        this.errors = errors;
    }

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public CalculationResultDTO getCalculation() {
        return calculation;
    }

    public void setCalculation(CalculationResultDTO calculation) {
        this.calculation = calculation;
    }

    public List<ErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDTO> errors) {
        this.errors = errors;
    }
}
