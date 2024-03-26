package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertControllerTest extends AbstractControllerTest {
    @Autowired
    private AlertRepository alertRepository;

    @AfterEach
    public void cleanUp() {
        alertRepository.deleteAll();
    }

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
        validationTests();
    }


    private void happyFlows() throws Exception {
        operators_can_get_their_alert();
        entity_admins_can_get_their_alert();
        business_admins_can_get_their_alert();
        sys_admins_can_get_any_alert();

        business_admins_can_create_an_alert_for_their_business();
        business_admins_can_update_an_alert_for_their_business();
        sys_admins_can_create_any_alert();
    }

    private void securityAccessTests() throws Exception {
        Group anotherBusiness = groupService.create(GroupSpec.builder()
                .parentGroupId(root.getId())
                .identifier(UUID.randomUUID().toString())
                .displayName(UUID.randomUUID() + " name")
                .type(GroupType.BUSINESS)
                .build());

        Long otherBusinessId = anotherBusiness.getId();

        operators_cannot_get_alerts_for_other_businesses(otherBusinessId);
        group_admins_cannot_get_alerts_for_other_businesses(otherBusinessId);
        business_admins_cannot_get_alerts_for_other_businesses(otherBusinessId);

        business_admins_cannot_create_an_alert_for_another_business(otherBusinessId);
        operators_cannot_create_alerts();
        business_admins_cannot_update_an_alert_for_another_business(otherBusinessId);
        operators_cannot_update_alerts();
    }

    private void validationTests() throws Exception {
        should_fail_validation_with_end_date_before_start_date();
        should_fail_validation_with_too_long_fields();
        should_fail_validation_with_null_fields();
    }


    private void operators_can_get_their_alert() throws Exception {
        runGetAlertTest(operatorUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void entity_admins_can_get_their_alert() throws Exception {
        runGetAlertTest(entityAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void business_admins_can_get_their_alert() throws Exception {
        runGetAlertTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void sys_admins_can_get_any_alert() throws Exception {
        runGetAlertTest(sysAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void operators_cannot_get_alerts_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertTest(operatorUserDetails, otherBusinessId, status().isForbidden());
    }

    private void group_admins_cannot_get_alerts_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertTest(entityAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void business_admins_cannot_get_alerts_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void business_admins_can_create_an_alert_for_their_business() throws Exception {
        runCreateTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isCreated());
    }

    private void business_admins_cannot_create_an_alert_for_another_business(Long otherBusinessId) throws Exception {
        runCreateTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void operators_cannot_create_alerts() throws Exception {
        runCreateTest(operatorUserDetails, abstractTestBusiness.getId(), status().isForbidden());
    }

    private void business_admins_can_update_an_alert_for_their_business() throws Exception {
        runUpdateTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void business_admins_cannot_update_an_alert_for_another_business(Long otherBusinessId) throws Exception {
        runUpdateTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void operators_cannot_update_alerts() throws Exception {
        runUpdateTest(operatorUserDetails, abstractTestBusiness.getId(), status().isForbidden());
    }

    private void sys_admins_can_create_any_alert() throws Exception {
        runCreateTest(sysAdminUserDetails, abstractTestBusiness.getId(), status().isCreated());
    }

    private void should_fail_validation_with_null_fields() throws Exception {
        this.runAlertCreateOrUpdateValidationTest(
                AlertSpec.builder()
                        .isActive(true)
                        .build(),
                GROUP_ID_NOT_NULL_ERROR_MSG,
                TYPE_NOT_NULL_ERROR_MSG,
                TITLE_NOT_NULL_ERROR_MSG,
                CONTENT_NOT_NULL_ERROR_MSG,
                START_DATE_IS_REQUIRED_ERROR_MSG
        );
    }

    private void should_fail_validation_with_too_long_fields() throws Exception {
        this.runAlertCreateOrUpdateValidationTest(
                AlertSpec.builder()
                        .groupId(1L)
                        .type(AlertType.INFO)
                        .title(StringUtils.repeat("c", 256))
                        .content("aContent")
                        .isActive(false)
                        .build(),
                TITLE_LENGTH_ERROR_MSG
        );
    }

    private void should_fail_validation_with_end_date_before_start_date() throws Exception {
        AlertSpec alertSpec = mockAlertSpec(abstractTestBusiness.getId());
        alertSpec.setEndDate(Date.from(alertSpec.getStartDate().toInstant().minus(1, ChronoUnit.DAYS)));

        this.runAlertCreateOrUpdateValidationTest(
                alertSpec,
                END_DATE_CANNOT_PRECEDE_START_DATE_ERROR_MSG
        );
    }

    private void runGetAlertTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(
                get(UrlTemplates.ALERT, businessId)
                        .with(user(userDetails)
                        )
        ).andExpect(expectedResult);
    }

    private void runCreateTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        AlertSpec alertSpec = mockAlertSpec(businessId);

        mockMvc.perform(
                post(UrlTemplates.ALERTS)
                        .with(user(userDetails)
                        ).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alertSpec)
                        )
        ).andExpect(expectedResult);
    }

    private void runUpdateTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        AlertSpec alertSpec = mockAlertSpec(businessId);

        mockMvc.perform(
                put(UrlTemplates.ALERTS)
                        .with(user(userDetails)
                        ).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(alertSpec)
                        )
        ).andExpect(expectedResult);
    }

    private void runAlertCreateOrUpdateValidationTest(AlertSpec alertSpec, String... expectedErrors) throws Exception {
        MvcResult putResult = mockMvc.perform(
                        put(UrlTemplates.ALERTS)
                                .with(user(sysAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(alertSpec)
                                )
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        MvcResult postResult = mockMvc.perform(
                        post(UrlTemplates.ALERTS)
                                .with(user(sysAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(alertSpec)
                                )
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(putResult.getResponse(), expectedErrors);
        ValidationTestUtils.checkValidationErrors(postResult.getResponse(), expectedErrors);
    }

    private AlertSpec mockAlertSpec(Long businessId) {
        Date startDate = new Date();
        return AlertSpec.builder()
                .title("title")
                .groupId(businessId)
                .content("Some content")
                .type(AlertType.INFO)
                .isActive(true)
                .startDate(startDate)
                .endDate(Date.from(startDate.toInstant().plus(1, ChronoUnit.DAYS)))
                .build();
    }
}
