package com.muni.rcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SearchRequestDTO {

    @NotNull(message = "RequestInfo is required")
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @Valid
    @JsonProperty("SearchCriteria")
    private SearchCriteriaDTO searchCriteria;

    public SearchRequestDTO() {}

    public SearchRequestDTO(RequestInfo requestInfo, SearchCriteriaDTO searchCriteria) {
        this.requestInfo = requestInfo;
        this.searchCriteria = searchCriteria;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public SearchCriteriaDTO getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(SearchCriteriaDTO searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
