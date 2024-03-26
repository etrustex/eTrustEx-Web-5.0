package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationRequestControllerTest extends AbstractControllerTest {

    @Autowired
    private UserRegistrationRedirectRepository userRegistrationRedirectRepository;

    @Test
    void integrationTest() throws Exception {
        happyFlows();
    }

    private void happyFlows() throws Exception {
        should_create_user_registration_request();
    }

    void should_create_user_registration_request() throws Exception {
        userRegistrationRedirectRepository.save(UserRegistrationRedirect.builder()
                .groupIdentifier(abstractTestEntity.getIdentifier())
                .groupId(abstractTestEntity.getId())
                        .emailAddress(otherUser.getEuLoginEmailAddress())
                .build()
        );

        MvcResult result = mockMvc.perform(post(UrlTemplates.USER_REGISTRATION)
                        .with(user(businessAdminUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUserRegistrationRequestSpec())))
                .andExpect(status().isCreated())
                .andReturn();

        UserRegistrationRequest userRegistrationRequest = objectMapper.readValue(result.getResponse().getContentAsString(), UserRegistrationRequest.class);

        assertNotNull(userRegistrationRequest.getUser());
        assertNotNull(userRegistrationRequest.getGroup());
    }

    private UserRegistrationRequestSpec mockUserRegistrationRequestSpec() {
        return UserRegistrationRequestSpec.builder()
                .ecasId(otherUser.getEcasId())
                .groupIdentifier(abstractTestEntity.getIdentifier())
                .groupId(abstractTestEntity.getId())
                .notificationEmail("test@gmail.com")
                .requesterEcasId("requesterEcasId")
                .requesterEmailAddress(otherUser.getEuLoginEmailAddress())
                .build();
    }

}
