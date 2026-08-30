package com.muni.rcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muni.rcp.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestInfo createRequestInfo(String role, String tenantId, String userName) {
        RequestInfo req = new RequestInfo();
        req.setMsgId("test-msg-1|en_IN");
        RequestInfo.UserInfo user = new RequestInfo.UserInfo(
                "user-" + userName,
                userName,
                tenantId,
                List.of(new RequestInfo.RoleInfo(role))
        );
        req.setUserInfo(user);
        return req;
    }

    @Test
    void testCalculateApi_WorkedExampleA() throws Exception {
        CalculationInputDTO calcInput = new CalculationInputDTO(
                "dehradun",
                "BT",
                new BigDecimal("12.5"),
                new BigDecimal("1.2"),
                6,
                "PRIVATE",
                LocalDate.now().plusDays(1),
                LocalDate.now()
        );
        CalculationRequestDTO request = new CalculationRequestDTO(
                createRequestInfo("APPLICANT", "dehradun", "9990000001"),
                calcInput
        );

        mockMvc.perform(post("/rcp/v1/_calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ResponseInfo.status", is("successful")))
                .andExpect(jsonPath("$.Calculation.areaInSqm", is(15)))
                .andExpect(jsonPath("$.Calculation.restorationCharge", is(18000.00)))
                .andExpect(jsonPath("$.Calculation.permissionFee", is(1350.00)))
                .andExpect(jsonPath("$.Calculation.urgencySurcharge", is(135.00)))
                .andExpect(jsonPath("$.Calculation.securityDeposit", is(5000.00)))
                .andExpect(jsonPath("$.Calculation.totalAmount", is(24485.00)))
                .andExpect(jsonPath("$.Calculation.reviewRef", is("K7Q2")));
    }

    @Test
    void testFullApplicationLifecycleApi() throws Exception {
        ApplicationCreateDTO createDTO = new ApplicationCreateDTO();
        createDTO.setTenantId("dehradun");
        createDTO.setRoadType("BT");
        createDTO.setLengthInMeters(new BigDecimal("12.5"));
        createDTO.setWidthInMeters(new BigDecimal("1.2"));
        createDTO.setDurationInDays(6);
        createDTO.setApplicantType("PRIVATE");
        createDTO.setProposedStartDate(LocalDate.now().plusDays(2));
        createDTO.setApplicantName("Ramesh Kumar");
        createDTO.setApplicantMobile("9990000001");
        createDTO.setLocation("Rajpur Road, Near Clock Tower");
        createDTO.setDescription("Fibre optic cable laying");

        ApplicationRequestDTO createRequest = new ApplicationRequestDTO(
                createRequestInfo("APPLICANT", "dehradun", "9990000001"),
                createDTO
        );

        String createResponseStr = mockMvc.perform(post("/rcp/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ResponseInfo.status", is("successful")))
                .andExpect(jsonPath("$.Application.status", is("APPLIED")))
                .andExpect(jsonPath("$.Application.applicationNumber", containsString("DDN-RCP-")))
                .andExpect(jsonPath("$.Application.calculation.totalAmount", is(24485.00)))
                .andExpect(jsonPath("$.Application.timeline", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        ApplicationResponseDTO createResp = objectMapper.readValue(createResponseStr, ApplicationResponseDTO.class);
        String appNum = createResp.getApplication().getApplicationNumber();

        ApplicationActionRequestDTO verifyRequest = new ApplicationActionRequestDTO(
                createRequestInfo("VERIFIER", "dehradun", "je_dehradun"),
                new ApplicationActionDTO(appNum, "VERIFY", "Site inspection completed, clear to proceed")
        );

        mockMvc.perform(post("/rcp/v1/_action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Application.status", is("PENDING_APPROVAL")))
                .andExpect(jsonPath("$.Application.timeline", hasSize(2)));

        ApplicationActionRequestDTO approveRequest = new ApplicationActionRequestDTO(
                createRequestInfo("APPROVER", "dehradun", "ee_dehradun"),
                new ApplicationActionDTO(appNum, "APPROVE", "Permission granted with standard safety guidelines")
        );

        mockMvc.perform(post("/rcp/v1/_action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Application.status", is("APPROVED")))
                .andExpect(jsonPath("$.Application.timeline", hasSize(3)));
    }

    @Test
    void testTenantIsolationEnforcement() throws Exception {
        ApplicationCreateDTO createDTO = new ApplicationCreateDTO();
        createDTO.setTenantId("dehradun");
        createDTO.setRoadType("CC");
        createDTO.setLengthInMeters(new BigDecimal("10.0"));
        createDTO.setWidthInMeters(new BigDecimal("1.0"));
        createDTO.setDurationInDays(3);
        createDTO.setApplicantType("PRIVATE");
        createDTO.setProposedStartDate(LocalDate.now().plusDays(5));
        createDTO.setApplicantName("Anita Sharma");
        createDTO.setApplicantMobile("9888888888");
        createDTO.setLocation("Astley Hall");

        ApplicationRequestDTO createRequest = new ApplicationRequestDTO(
                createRequestInfo("APPLICANT", "dehradun", "9888888888"),
                createDTO
        );

        String responseStr = mockMvc.perform(post("/rcp/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ApplicationResponseDTO createResp = objectMapper.readValue(responseStr, ApplicationResponseDTO.class);
        String dehradunAppNum = createResp.getApplication().getApplicationNumber();

        ApplicationActionRequestDTO illegalHaridwarAction = new ApplicationActionRequestDTO(
                createRequestInfo("VERIFIER", "haridwar", "je_haridwar"),
                new ApplicationActionDTO(dehradunAppNum, "VERIFY", "Attempting cross tenant verify")
        );

        mockMvc.perform(post("/rcp/v1/_action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(illegalHaridwarAction)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ResponseInfo.status", is("failed")))
                .andExpect(jsonPath("$.Errors[0].code", is("NOT_FOUND")));
    }

    @Test
    void testSearchByApplicationNumber() throws Exception {
        ApplicationCreateDTO createDTO = new ApplicationCreateDTO();
        createDTO.setTenantId("dehradun");
        createDTO.setRoadType("BT");
        createDTO.setLengthInMeters(new BigDecimal("15.0"));
        createDTO.setWidthInMeters(new BigDecimal("2.0"));
        createDTO.setDurationInDays(4);
        createDTO.setApplicantType("PRIVATE");
        createDTO.setProposedStartDate(LocalDate.now().plusDays(10));
        createDTO.setApplicantName("Search Test User");
        createDTO.setApplicantMobile("9876540001");
        createDTO.setLocation("Paltan Bazaar");

        ApplicationRequestDTO createRequest = new ApplicationRequestDTO(
                createRequestInfo("APPLICANT", "dehradun", "9876540001"),
                createDTO
        );

        String responseStr = mockMvc.perform(post("/rcp/v1/_create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ApplicationResponseDTO createResp = objectMapper.readValue(responseStr, ApplicationResponseDTO.class);
        String appNum = createResp.getApplication().getApplicationNumber();

        SearchCriteriaDTO exactCriteria = new SearchCriteriaDTO();
        exactCriteria.setApplicationNumber(appNum);
        SearchRequestDTO searchExact = new SearchRequestDTO(
                createRequestInfo("VERIFIER", "dehradun", "je_dehradun"),
                exactCriteria
        );

        mockMvc.perform(post("/rcp/v1/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchExact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ResponseInfo.status", is("successful")))
                .andExpect(jsonPath("$.Applications", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.Applications[0].applicationNumber", is(appNum)));

        SearchCriteriaDTO lowerCriteria = new SearchCriteriaDTO();
        lowerCriteria.setApplicationNumber(appNum.toLowerCase());

        String sequencePart = appNum.substring(appNum.indexOf("-RCP-") + 5, appNum.lastIndexOf("-"));
        SearchCriteriaDTO partialCriteria = new SearchCriteriaDTO();
        partialCriteria.setApplicationNumber(sequencePart);
        SearchRequestDTO searchPartial = new SearchRequestDTO(
                createRequestInfo("VERIFIER", "dehradun", "je_dehradun"),
                partialCriteria
        );

        mockMvc.perform(post("/rcp/v1/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchPartial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ResponseInfo.status", is("successful")));
    }
}
