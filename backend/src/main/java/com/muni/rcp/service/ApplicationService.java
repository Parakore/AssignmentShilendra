package com.muni.rcp.service;

import com.muni.rcp.config.RateProvider;
import com.muni.rcp.dto.*;
import com.muni.rcp.entity.ApplicationEntity;
import com.muni.rcp.exception.InvalidInputException;
import com.muni.rcp.exception.ResourceNotFoundException;
import com.muni.rcp.exception.TenantMismatchException;
import com.muni.rcp.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final CalculationService calculationService;
    private final SequenceService sequenceService;
    private final WorkflowService workflowService;
    private final RateProvider rateProvider;

    private final int maxSearchLimit;
    private final int defaultSearchLimit;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            CalculationService calculationService,
            SequenceService sequenceService,
            WorkflowService workflowService,
            RateProvider rateProvider,
            @Value("${rcp.max-search-limit:50}") int maxSearchLimit,
            @Value("${rcp.default-search-limit:20}") int defaultSearchLimit) {
        this.applicationRepository = applicationRepository;
        this.calculationService = calculationService;
        this.sequenceService = sequenceService;
        this.workflowService = workflowService;
        this.rateProvider = rateProvider;
        this.maxSearchLimit = maxSearchLimit;
        this.defaultSearchLimit = defaultSearchLimit;
    }

    @Transactional
    public ApplicationDetailDTO createApplication(ApplicationCreateDTO createDTO, RequestInfo requestInfo) {
        log.info("Creating application {}", createDTO);
        validateCallerTenant(createDTO.getTenantId(), requestInfo);

        LocalDate now = LocalDate.now();
        CalculationInputDTO calcInput = new CalculationInputDTO(
                createDTO.getTenantId(),
                createDTO.getRoadType(),
                createDTO.getLengthInMeters(),
                createDTO.getWidthInMeters(),
                createDTO.getDurationInDays(),
                createDTO.getApplicantType(),
                createDTO.getProposedStartDate(),
                now
        );

        CalculationResultDTO calcResult = calculationService.calculateFee(calcInput);

        String actorUuid = workflowService.extractActorUuid(requestInfo);
        long epochNow = System.currentTimeMillis();

        String applicationNumber = sequenceService.generateNextApplicationNumber(
                createDTO.getTenantId(),
                now,
                actorUuid
        );

        ApplicationEntity entity = new ApplicationEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setApplicationNumber(applicationNumber);
        entity.setTenantId(createDTO.getTenantId().toLowerCase().trim());
        entity.setStatus("APPLIED");
        entity.setApplicantName(createDTO.getApplicantName().trim());
        entity.setApplicantMobile(createDTO.getApplicantMobile().trim());
        entity.setApplicantEmail(createDTO.getApplicantEmail());
        entity.setApplicantType(createDTO.getApplicantType().toUpperCase().trim());
        entity.setRoadType(createDTO.getRoadType().toUpperCase().trim());
        entity.setLengthInMeters(createDTO.getLengthInMeters());
        entity.setWidthInMeters(createDTO.getWidthInMeters());
        entity.setAreaInSqm(calcResult.getAreaInSqm());
        entity.setDurationInDays(createDTO.getDurationInDays());
        entity.setProposedStartDate(createDTO.getProposedStartDate());
        entity.setApplicationDate(now);
        entity.setLocation(createDTO.getLocation().trim());
        entity.setDescription(createDTO.getDescription());

        entity.setRestorationCharge(calcResult.getRestorationCharge());
        entity.setPermissionFee(calcResult.getPermissionFee());
        entity.setUrgencySurcharge(calcResult.getUrgencySurcharge());
        entity.setSecurityDeposit(calcResult.getSecurityDeposit());
        entity.setTotalAmount(calcResult.getTotalAmount());

        entity.setCreatedBy(actorUuid);
        entity.setCreatedTime(epochNow);
        entity.setLastModifiedBy(actorUuid);
        entity.setLastModifiedTime(epochNow);

        ApplicationEntity saved = applicationRepository.save(entity);
        workflowService.recordInitialHistory(saved, requestInfo);

        return mapToDetailDTO(saved, requestInfo);
    }

    @Transactional
    public ApplicationDetailDTO applyAction(
            ApplicationActionDTO actionDTO,
            ApplicationCreateDTO editPayload,
            RequestInfo requestInfo) {

        if (actionDTO == null || actionDTO.getApplicationNumber() == null || actionDTO.getAction() == null) {
            throw new InvalidInputException("Action and applicationNumber are required");
        }

        String callerTenant = extractCallerTenant(requestInfo);
        String appNum = actionDTO.getApplicationNumber().trim();

        ApplicationEntity application = applicationRepository
                .findByTenantIdAndApplicationNumber(callerTenant, appNum)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Application '%s' not found for tenant '%s'", appNum, callerTenant)
                ));

        if (!application.getTenantId().equalsIgnoreCase(callerTenant)) {
            throw new TenantMismatchException("Access denied: Tenant isolation violation");
        }

        String action = actionDTO.getAction().toUpperCase().trim();

        if ("EDIT".equals(action)) {
            if (!"APPLIED".equalsIgnoreCase(application.getStatus())) {
                throw new InvalidInputException("Applications can only be edited when in APPLIED status");
            }
            if (editPayload != null) {
                updateApplicationDetails(application, editPayload);
            }
        }

        String nextStatus = workflowService.validateAndExecuteTransition(
                application,
                action,
                actionDTO.getComment(),
                requestInfo
        );

        String actorUuid = workflowService.extractActorUuid(requestInfo);
        long epochNow = System.currentTimeMillis();

        application.setStatus(nextStatus);
        application.setLastModifiedBy(actorUuid);
        application.setLastModifiedTime(epochNow);

        ApplicationEntity updated = applicationRepository.save(application);
        return mapToDetailDTO(updated, requestInfo);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDetailDTO> searchApplications(SearchCriteriaDTO criteria, RequestInfo requestInfo) {
        String callerTenant = extractCallerTenant(requestInfo);
        log.info("Searching applications for tenant: {}, criteria: {}", callerTenant, criteria);
        List<String> roles = workflowService.extractRoles(requestInfo);

        String searchTenant = callerTenant;
        String appNum = (criteria != null && criteria.getApplicationNumber() != null && !criteria.getApplicationNumber().isBlank())
                ? criteria.getApplicationNumber().trim() : null;
        String status = (criteria != null && criteria.getStatus() != null && !criteria.getStatus().isBlank())
                ? criteria.getStatus().toUpperCase().trim() : null;
        String mobile = (criteria != null && criteria.getMobileNumber() != null && !criteria.getMobileNumber().isBlank())
                ? criteria.getMobileNumber().trim() : null;

        boolean isOfficer = roles.stream().anyMatch(r -> "VERIFIER".equalsIgnoreCase(r) || "APPROVER".equalsIgnoreCase(r));
        if (!isOfficer && mobile == null && appNum == null) {
            String userPhone = (requestInfo.getUserInfo() != null && requestInfo.getUserInfo().getMobileNumber() != null)
                    ? requestInfo.getUserInfo().getMobileNumber()
                    : (requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUserName() : null);
            if (userPhone != null && !userPhone.isBlank()) {
                mobile = userPhone.trim();
            }
        }

        int offset = (criteria != null) ? criteria.getOffset() : 0;
        int limit = (criteria != null && criteria.getLimit() != null) ? criteria.getLimit() : defaultSearchLimit;
        if (limit > maxSearchLimit) {
            limit = maxSearchLimit;
        }
        if (limit <= 0) {
            limit = defaultSearchLimit;
        }

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        Page<ApplicationEntity> page = applicationRepository.searchApplications(
                searchTenant, appNum, status, mobile, pageable
        );

        return page.getContent().stream()
                .map(entity -> mapToDetailDTO(entity, requestInfo))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDetailDTO getByApplicationNumber(String applicationNumber, RequestInfo requestInfo) {
        log.info("");
        String callerTenant = extractCallerTenant(requestInfo);
        ApplicationEntity entity = applicationRepository
                .findByTenantIdAndApplicationNumber(callerTenant, applicationNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Application '%s' not found for tenant '%s'", applicationNumber, callerTenant)
                ));
        return mapToDetailDTO(entity, requestInfo);
    }

    private void updateApplicationDetails(ApplicationEntity entity, ApplicationCreateDTO payload) {
        if (payload.getRoadType() != null && !payload.getRoadType().isBlank()) {
            entity.setRoadType(payload.getRoadType().toUpperCase().trim());
        }
        if (payload.getLengthInMeters() != null) {
            entity.setLengthInMeters(payload.getLengthInMeters());
        }
        if (payload.getWidthInMeters() != null) {
            entity.setWidthInMeters(payload.getWidthInMeters());
        }
        if (payload.getDurationInDays() != null) {
            entity.setDurationInDays(payload.getDurationInDays());
        }
        if (payload.getProposedStartDate() != null) {
            entity.setProposedStartDate(payload.getProposedStartDate());
        }
        if (payload.getLocation() != null) {
            entity.setLocation(payload.getLocation());
        }
        if (payload.getDescription() != null) {
            entity.setDescription(payload.getDescription());
        }

        CalculationInputDTO calcInput = new CalculationInputDTO(
                entity.getTenantId(),
                entity.getRoadType(),
                entity.getLengthInMeters(),
                entity.getWidthInMeters(),
                entity.getDurationInDays(),
                entity.getApplicantType(),
                entity.getProposedStartDate(),
                entity.getApplicationDate()
        );
        CalculationResultDTO calcResult = calculationService.calculateFee(calcInput);
        entity.setAreaInSqm(calcResult.getAreaInSqm());
        entity.setRestorationCharge(calcResult.getRestorationCharge());
        entity.setPermissionFee(calcResult.getPermissionFee());
        entity.setUrgencySurcharge(calcResult.getUrgencySurcharge());
        entity.setSecurityDeposit(calcResult.getSecurityDeposit());
        entity.setTotalAmount(calcResult.getTotalAmount());
    }

    public ApplicationDetailDTO mapToDetailDTO(ApplicationEntity entity, RequestInfo requestInfo) {
        List<String> roles = workflowService.extractRoles(requestInfo);
        List<String> allowedActions = workflowService.getAllowedNextActions(entity.getStatus(), roles);
        List<ActionHistoryDTO> timeline = workflowService.getTimeline(entity.getTenantId(), entity.getId());

        String roadTypeName = entity.getRoadType();
        try {
            var rtRate = rateProvider.getRoadTypeRate(entity.getTenantId(), entity.getRoadType());
            roadTypeName = rtRate.name();
        } catch (Exception ignored) {
        }

        CalculationResultDTO calcResult = new CalculationResultDTO(
                entity.getAreaInSqm(),
                entity.getRestorationCharge(),
                entity.getPermissionFee(),
                entity.getUrgencySurcharge(),
                entity.getSecurityDeposit(),
                entity.getTotalAmount(),
                "K7Q2"
        );

        ApplicationDetailDTO dto = new ApplicationDetailDTO();
        dto.setId(entity.getId());
        dto.setApplicationNumber(entity.getApplicationNumber());
        dto.setTenantId(entity.getTenantId());
        dto.setStatus(entity.getStatus());
        dto.setApplicantName(entity.getApplicantName());
        dto.setApplicantMobile(entity.getApplicantMobile());
        dto.setApplicantEmail(entity.getApplicantEmail());
        dto.setApplicantType(entity.getApplicantType());
        dto.setRoadType(entity.getRoadType());
        dto.setRoadTypeName(roadTypeName);
        dto.setLengthInMeters(entity.getLengthInMeters());
        dto.setWidthInMeters(entity.getWidthInMeters());
        dto.setAreaInSqm(entity.getAreaInSqm());
        dto.setDurationInDays(entity.getDurationInDays());
        dto.setProposedStartDate(entity.getProposedStartDate());
        dto.setApplicationDate(entity.getApplicationDate());
        dto.setLocation(entity.getLocation());
        dto.setDescription(entity.getDescription());
        dto.setCalculation(calcResult);
        dto.setTimeline(timeline);
        dto.setAllowedActions(allowedActions);
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedTime(entity.getLastModifiedTime());
        dto.setLastModifiedBy(entity.getLastModifiedBy());

        return dto;
    }

    private void validateCallerTenant(String payloadTenant, RequestInfo requestInfo) {
        String callerTenant = extractCallerTenant(requestInfo);
        if (payloadTenant != null && !payloadTenant.isBlank() && !payloadTenant.equalsIgnoreCase(callerTenant)) {
            throw new TenantMismatchException(
                    String.format("Payload tenant '%s' does not match caller tenant '%s'", payloadTenant, callerTenant)
            );
        }
    }

    public String extractCallerTenant(RequestInfo requestInfo) {
        if (requestInfo != null && requestInfo.getUserInfo() != null && requestInfo.getUserInfo().getTenantId() != null) {
            String t = requestInfo.getUserInfo().getTenantId().trim().toLowerCase();
            if (!t.isBlank()) {
                return t;
            }
        }
        return "dehradun";
    }
}
