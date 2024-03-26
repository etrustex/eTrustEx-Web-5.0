package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.exchange.RecipientPreferencesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecipientPreferencesControllerTest extends AbstractControllerTest {

    private static final String VALID_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
            "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
            "YWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
            "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
            "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
            "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
            "PQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";

    private final ResponseFieldsSnippet recipientPreferencesResponseFields = relaxedResponseFields(
            fieldWithPath("id").description("The UUID of the recipient preferences"),
            fieldWithPath("publicKey").description("The publicKey"),
            fieldWithPath("confidentiality").description("The confidentiality levelof the message"),
            fieldWithPath("integrity").description("The integrity "),
            fieldWithPath("availability").description("The availability of the message")
    );
    @Autowired
    private RecipientPreferencesService recipientPreferencesService;

    private SecurityUserDetails senderUserDetails;
    private RecipientPreferencesSpec recipientPreferencesSpec;
    private RecipientPreferences recipientPreferences;

    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
        validationTests();
    }

    void initConfigurations() {
        recipientPreferencesSpec = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey(VALID_PUBLIC_KEY)
                .publicKeyFileName("public_key.pub")
                .build();

        recipientPreferences = recipientPreferencesService.create(recipientPreferencesSpec);


        User testUser = userService.findByEcasId(TEST_USER_ID);
        userUtils.createUserProfile(testUser.getEcasId(), abstractTestEntity.getId(), businessAdminUserDetails, mockMvc);
        senderUserDetails = userUtils.buildUserDetails(testUser, abstractTestEntity, RoleName.GROUP_ADMIN);
        operatorUserDetails = userUtils.buildUserDetails(testUser, abstractTestEntity, RoleName.OPERATOR);
    }

    private void happyFlows() throws Exception {
        should_create(senderUserDetails);
        should_get_the_recipient_preferences_with_sys_admin_role(recipientPreferences.getId());
        should_get_a_recipient_preferences_with_group_admin_role(recipientPreferences.getId());
        should_update_an_existing_recipient_preferences();
    }

    private void securityAccessTests() throws Exception {
        should_forbid_an_operator_to_create(operatorUserDetails);
        should_forbid_get_for_operator_role(recipientPreferences.getId(), operatorUserDetails);
        should_forbid_get_when_the_recipient_is_not_linked_to_a_group();
        should_forbid_update_when_the_recipient_is_not_linked_to_a_group();

    }

    private void validationTests() throws Exception {
        should_fail_validation_for_create();
        should_fail_validation_for_update();
    }

    private void should_create(SecurityUserDetails userDetails) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(UrlTemplates.RECIPIENTS_PREFERENCES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andDo(document(
                        "should_create_a_recipient_preferences",
                        recipientPreferencesResponseFields,
                        RestTestUtils.recipientPreferencesHateoasLinks
                ))
                .andExpect(status().isCreated());

        MvcResult result = resultActions.andReturn();

        RecipientPreferences recipientPrefs = objectMapper.readValue(result.getResponse().getContentAsString(), RecipientPreferences.class);
        assertNotNull(recipientPrefs.getId());
        assertFalse(recipientPrefs.getLinks().isEmpty());

    }

    private void should_forbid_an_operator_to_create(SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(post(UrlTemplates.RECIPIENTS_PREFERENCES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andExpect(status().isForbidden());

    }

    private void should_forbid_get_when_the_recipient_is_not_linked_to_a_group() throws Exception {
        abstractTestEntity.setRecipientPreferences(null);
        groupRepository.save(abstractTestEntity);

        mockMvc.perform(get(UrlTemplates.RECIPIENT_PREFERENCES, recipientPreferences.getId())
                        .with(user(senderUserDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_get_the_recipient_preferences_with_sys_admin_role(Long id) throws Exception {
        MvcResult result = mockMvc.perform(get(UrlTemplates.RECIPIENT_PREFERENCES, id)
                        .with(user(sysAdminUserDetails)))
                .andExpect(status().isOk())
                .andReturn();

        RecipientPreferences recipientPrefs = objectMapper.readValue(result.getResponse().getContentAsString(), RecipientPreferences.class);
        assertEquals(id, recipientPrefs.getId());
        assertFalse(recipientPrefs.getLinks().isEmpty());
    }

    private void should_forbid_get_for_operator_role(Long id, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(get(UrlTemplates.RECIPIENT_PREFERENCES, id)
                        .with(user(userDetails)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private void should_get_a_recipient_preferences_with_group_admin_role(Long id) throws Exception {
        abstractTestEntity.setRecipientPreferences(recipientPreferences);
        groupRepository.save(abstractTestEntity);

        MvcResult result = mockMvc.perform(get(UrlTemplates.RECIPIENT_PREFERENCES, id)
                        .with(user(senderUserDetails)))
                .andExpect(status().isOk())
                .andReturn();

        RecipientPreferences recipientPrefs = objectMapper.readValue(result.getResponse().getContentAsString(), RecipientPreferences.class);
        assertEquals(id, recipientPrefs.getId());
        assertFalse(recipientPrefs.getLinks().isEmpty());

    }

    private void should_update_an_existing_recipient_preferences() throws Exception {
        recipientPreferencesSpec.setConfidentiality(Confidentiality.LIMITED_HIGH);
        MvcResult result = mockMvc.perform(put(UrlTemplates.RECIPIENT_PREFERENCES, recipientPreferences.getId())
                        .with(user(senderUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andDo(document(
                        "should_update_an_existing_recipient_preferences",
                        recipientPreferencesResponseFields,
                        RestTestUtils.recipientPreferencesHateoasLinks
                ))
                .andExpect(status().isOk())
                .andReturn();


        RecipientPreferences recipientPrefs = objectMapper.readValue(result.getResponse().getContentAsString(), RecipientPreferences.class);
        assertEquals(Confidentiality.LIMITED_HIGH, recipientPrefs.getConfidentiality());
    }

    private void should_forbid_update_when_the_recipient_is_not_linked_to_a_group() throws Exception {
        mockMvc.perform(put(UrlTemplates.RECIPIENT_PREFERENCES, recipientPreferences.getId())
                        .with(user(senderUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andExpect(status().isForbidden());
    }

    private void should_fail_validation_for_create() throws Exception {
        recipientPreferencesSpec.setPublicKey("Invalid");
        mockMvc.perform(post(UrlTemplates.RECIPIENTS_PREFERENCES)
                        .with(user(senderUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andExpect(status().isBadRequest());
    }

    private void should_fail_validation_for_update() throws Exception {
        abstractTestEntity.setRecipientPreferences(recipientPreferences);
        groupRepository.save(abstractTestEntity);
        recipientPreferencesSpec.setConfidentiality(Confidentiality.SECRET_UE);
        mockMvc.perform(put(UrlTemplates.RECIPIENT_PREFERENCES, recipientPreferences.getId())
                        .with(user(senderUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipientPreferencesSpec)))
                .andExpect(status().isBadRequest());
    }

}
