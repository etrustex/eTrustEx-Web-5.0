package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.exchange.AlertType;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.AlertUserStatusRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AlertUserStatusService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlertUserStatusControllerTest extends AbstractControllerTest {
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private AlertUserStatusRepository alertUserStatusRepository;
    @Autowired
    private AlertUserStatusService alertUserStatusService;

    @AfterEach
    public void cleanUp() {
        alertUserStatusRepository.deleteAll();
        alertRepository.deleteAll();
    }

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
    }

    private void happyFlows() throws Exception {
        operators_can_get_their_alert_user_status();
        entity_admins_can_get_their_alert_user_status();
        business_admins_can_get_their_alert_user_status();

        operator_can_create_an_alert_user_status_for_their_business();
        entity_admins_can_create_an_alert_user_status_for_their_business();
        business_admins_can_update_an_alert_user_status_for_their_business();
    }

    private void securityAccessTests() throws Exception {
        Long notAllowedBusinessId = 120L;
        Group anotherBusiness = groupService.create(GroupSpec.builder()
                .parentGroupId(root.getId())
                .identifier(UUID.randomUUID().toString())
                .displayName(UUID.randomUUID() + " name")
                .type(GroupType.BUSINESS)
                .build());

        Long otherBusinessId = anotherBusiness.getId();

        operators_cannot_get_alert_user_status_for_other_businesses(otherBusinessId);
        group_admins_cannot_get_alert_user_status_for_other_businesses(otherBusinessId);
        business_admins_cannot_get_alert_user_status_for_other_businesses(otherBusinessId);

        business_admins_cannot_create_an_alert_user_status_for_another_business(notAllowedBusinessId);
        operators_cannot_create_alert_user_statuses(notAllowedBusinessId);
        business_admins_cannot_update_an_alert_for_another_business(notAllowedBusinessId);
        operators_cannot_update_alerts(notAllowedBusinessId);
    }

    private void operators_can_get_their_alert_user_status() throws Exception {
        runGetAlertUserStatusTest(operatorUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void entity_admins_can_get_their_alert_user_status() throws Exception {
        runGetAlertUserStatusTest(entityAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void business_admins_can_get_their_alert_user_status() throws Exception {
        runGetAlertUserStatusTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void operators_cannot_get_alert_user_status_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertUserStatusTest(operatorUserDetails, otherBusinessId, status().isForbidden());
    }

    private void group_admins_cannot_get_alert_user_status_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertUserStatusTest(entityAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void business_admins_cannot_get_alert_user_status_for_other_businesses(Long otherBusinessId) throws Exception {
        runGetAlertUserStatusTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void operator_can_create_an_alert_user_status_for_their_business() throws Exception {
        runCreateAlertUserStatusTest(operatorUserDetails, abstractTestBusiness.getId(), status().isCreated());
    }

    private void entity_admins_can_create_an_alert_user_status_for_their_business() throws Exception {
        runCreateAlertUserStatusTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isCreated());
    }

    private void business_admins_cannot_create_an_alert_user_status_for_another_business(Long otherBusinessId) throws Exception {
        runCreateAlertUserStatusTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void operators_cannot_create_alert_user_statuses(Long otherBusinessId) throws Exception {
        runCreateAlertUserStatusTest(operatorUserDetails, otherBusinessId, status().isForbidden());
    }

    private void business_admins_can_update_an_alert_user_status_for_their_business() throws Exception {
        runUpdateAlertUserStatusTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void business_admins_cannot_update_an_alert_for_another_business(Long otherBusinessId) throws Exception {
        runUpdateAlertUserStatusTest(businessAdminUserDetails, otherBusinessId, status().isForbidden());
    }

    private void operators_cannot_update_alerts(Long otherBusinessId) throws Exception {
        runUpdateAlertUserStatusTest(operatorUserDetails, otherBusinessId, status().isForbidden());
    }

    private void runGetAlertUserStatusTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(
                get(UrlTemplates.ALERT_USER_STATUS).param("businessId", String.valueOf(businessId))
                        .with(user(userDetails)
                        )
        ).andExpect(expectedResult);
    }

    private void runCreateAlertUserStatusTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        AlertUserStatusSpec alertUserStatusSpec = mockAlertUserStatusSpec(businessId);

        mockMvc.perform(
                post(UrlTemplates.ALERT_USER_STATUS)
                        .with(user(userDetails)
                        ).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alertUserStatusSpec)
                        )
        ).andExpect(expectedResult);
    }

    private void runUpdateAlertUserStatusTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        AlertUserStatusSpec alertUserStatusSpec = mockAlertUserStatusSpec(businessId);


        alertUserStatusService.create(alertUserStatusSpec);

        mockMvc.perform(
                put(UrlTemplates.ALERT_USER_STATUS)
                        .with(user(userDetails)
                        ).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(alertUserStatusSpec)
                        )
        ).andExpect(expectedResult);
    }
    private AlertUserStatusSpec mockAlertUserStatusSpec(Long businessId) throws Exception {
        Alert alert = createAlert();

        return AlertUserStatusSpec.builder()
                .groupId(businessId)
                .alertId(alert.getId())
                .userId(otherUser.getId())
                .status(AlertUserStatus.AlertUserStatusType.UNREAD.name())
                .build();
    }

    private Alert createAlert() throws Exception {
        Date startDate = new Date();
        AlertSpec alertSpec = AlertSpec.builder()
                .title("title")
                .groupId(abstractTestBusiness.getId())
                .content("Some content")
                .type(AlertType.INFO)
                .isActive(Boolean.TRUE)
                .startDate(startDate)
                .endDate(Date.from(startDate.toInstant().plus(1, ChronoUnit.DAYS)))
                .build();

        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.ALERTS)
                                .with(user(businessAdminUserDetails)
                                ).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(alertSpec)
                                )
                ).andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), Alert.class);
    }
}
