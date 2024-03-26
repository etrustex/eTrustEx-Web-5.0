package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRuleId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.RECIPIENT;
import static eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.SENDER;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedRequestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExchangeRuleControllerTest extends AbstractControllerTest {
    public static final String THE_ID_OF_THE_CHANNEL = "The id of the channel.";
    public static final String THE_PARTICIPATING_GROUP = "The participating group";
    public static final String IF_THE_GROUP_IS_A_SENDER_A_RECIPIENT_OR_BOTH_BIDIRECTIONAL = "The ExchangeMode. If the group is a SENDER, a RECIPIENT, or both (BIDIRECTIONAL)";
    public static final String CHANNEL_ID = "channelId";

    private Group recipientEntity;
    private Channel channel;
    private SecurityUserDetails senderOperatorUserDetails;
    private SecurityUserDetails otherBusinessUserDetails;

    private Group otherEntity;

    @Autowired
    private GroupUtils groupUtils;
    @Autowired
    private MessageUtils messageUtils;


    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
        validationTests();
    }


    private void initConfigurations() {
        Group senderEntity = groupUtils.createEntity(abstractTestBusiness.getId(), ExchangeRuleControllerTest.class.getSimpleName() + "_sender", businessAdminUserDetails);
        recipientEntity = groupUtils.createEntity(abstractTestBusiness.getId(), ExchangeRuleControllerTest.class.getSimpleName() + "_recipient1", businessAdminUserDetails);
        channel = messageUtils.createChannel(abstractTestBusiness.getId(), businessAdminUserDetails);

        messageUtils.createExchangeRule(channel.getId(), senderEntity.getId(), SENDER, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), recipientEntity.getId(), RECIPIENT, businessAdminUserDetails);

        UserProfile userProfile = userUtils.createUserProfile(TEST_USER_ID, senderEntity.getId(), businessAdminUserDetails, mockMvc);
        senderOperatorUserDetails = userUtils.buildUserDetails(userProfile.getUser(), senderEntity, RoleName.OPERATOR);

        Group otherBusiness = groupUtils.createBusiness(root.getId(), ExchangeRuleControllerTest.class.getSimpleName() + "_other_business", sysAdminUserDetails);
        otherBusinessUserDetails = userUtils.buildUserDetails(userProfile.getUser(), otherBusiness, RoleName.GROUP_ADMIN);

        otherEntity = groupUtils.createEntity(abstractTestBusiness.getId(), ExchangeRuleControllerTest.class.getSimpleName() + "_otherEntity", businessAdminUserDetails);
    }

    private void happyFlows() throws Exception {
        should_get_the_exchange_rules(sysAdminUserDetails, false);
        should_get_the_exchange_rules(businessAdminUserDetails, true);

        should_create_exchange_rule(otherEntity, sysAdminUserDetails, false);
        should_update_exchange_rule(otherEntity, sysAdminUserDetails, false);
        should_delete_exchange_rule(otherEntity.getId(), sysAdminUserDetails);
        should_create_exchange_rule(otherEntity, businessAdminUserDetails, true);
        should_update_exchange_rule(otherEntity, businessAdminUserDetails, true);
        should_delete_exchange_rule(otherEntity.getId(), businessAdminUserDetails);
    }

    private void securityAccessTests() throws Exception {
        should_not_get_the_exchange_rules(senderOperatorUserDetails);
        should_not_get_the_exchange_rules(otherBusinessUserDetails);

        should_not_create_exchange_rules(otherEntity, senderOperatorUserDetails);
        should_not_create_exchange_rules(otherEntity, otherBusinessUserDetails);


        should_not_delete_exchange_rules(recipientEntity.getId(), senderOperatorUserDetails);
        should_not_delete_exchange_rules(recipientEntity.getId(), otherBusinessUserDetails);
    }

    private void validationTests() throws Exception {
        ExchangeRuleSpec nonExistingExchangeRule = ExchangeRuleSpec.builder()
                .channelId(channel.getId())
                .memberId(RANDOM.nextLong())
                .exchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build();

        runUpdateValidationTest(nonExistingExchangeRule, businessAdminUserDetails, ValidationMessage.formatExchangeRuleExistsForChannelAndMemberErrorMessage(nonExistingExchangeRule.getChannelId(), nonExistingExchangeRule.getMemberId()));
    }

    private void should_get_the_exchange_rules(SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails)
                        ).param(CHANNEL_ID, String.valueOf(channel.getId())))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_get_the_exchange_rules_of_a_channel",
                    relaxedRequestParameters(
                            parameterWithName("page").optional().description("Page to retrieve"),
                            parameterWithName("size").optional().description("Maximum number of items in the response page."),
                            parameterWithName("sortBy").optional().description("Field by which the results must be sorted."),
                            parameterWithName("sortOrder").optional().description("Sorting direction (ASC/DEC)"),
                            parameterWithName(CHANNEL_ID).description(THE_ID_OF_THE_CHANNEL)
                    ),
                    relaxedResponseFields(
                            fieldWithPath("content[].member").description(THE_PARTICIPATING_GROUP),
                            fieldWithPath("content[].exchangeMode").description("If the group is a SENDER, a RECIPIENT, or both (BIDIRECTIONAL)")
                    )
            ));
        }

        MvcResult result = resultActions.andReturn();
        RestResponsePage<?> page = objectMapper.readValue(result.getResponse().getContentAsString(), RestResponsePage.class);

        assertThat(page.getContent().size()).isNotZero();
    }

    private void should_not_get_the_exchange_rules(SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(get(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails))
                        .param(CHANNEL_ID, String.valueOf(channel.getId())))
                .andExpect(status().isForbidden());
    }

    private void should_create_exchange_rule(Group entity, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        List<ExchangeRuleSpec> spec = Collections.singletonList(ExchangeRuleSpec.builder()
                .channelId(channel.getId())
                .memberId(entity.getId())
                .exchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build());

        ResultActions resultActions = mockMvc.perform(post(UrlTemplates.EXCHANGE_RULES_BULK)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spec)
                        ))
                .andExpect(status().isCreated());

        if (withRestDocs) {
            resultActions.andDo(document("should_create_the_exchange_rules_of_a_channel",
                    relaxedRequestFields(
                            fieldWithPath("[].channelId").description(THE_ID_OF_THE_CHANNEL),
                            fieldWithPath("[].memberId").description("The id of the participant Entity."),
                            fieldWithPath("[].exchangeMode").description(IF_THE_GROUP_IS_A_SENDER_A_RECIPIENT_OR_BOTH_BIDIRECTIONAL)
                    ),
                    relaxedResponseFields(
                            fieldWithPath("[].member").description(THE_PARTICIPATING_GROUP),
                            fieldWithPath("[].exchangeMode").description(IF_THE_GROUP_IS_A_SENDER_A_RECIPIENT_OR_BOTH_BIDIRECTIONAL)
                    )
            ));
        }

        MvcResult result = resultActions.andReturn();
        List<ExchangeRule> exchangeRules = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ExchangeRule>>() {});

        assertEquals(spec.get(0).getMemberId(), exchangeRules.get(0).getMember().getId());
        assertEquals(spec.get(0).getExchangeMode(), exchangeRules.get(0).getExchangeMode());
    }

    private void should_not_create_exchange_rules(Group entity, SecurityUserDetails userDetails) throws Exception {
        List<ExchangeRuleSpec> spec = Collections.singletonList(ExchangeRuleSpec.builder()
                .channelId(channel.getId())
                .memberId(entity.getId())
                .exchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build());

        mockMvc.perform(post(UrlTemplates.EXCHANGE_RULES_BULK)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spec)
                        ))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private void should_delete_exchange_rule(Long memberId, SecurityUserDetails userDetails) throws Exception {
        ExchangeRuleId exchangeRuleId = new ExchangeRuleId(channel.getId(), memberId);

        mockMvc.perform(delete(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchangeRuleId)
                        ))
                .andExpect(status().isOk())
                .andReturn();
    }

    private void should_not_delete_exchange_rules(Long memberId, SecurityUserDetails userDetails) throws Exception {
        ExchangeRuleId exchangeRuleId = new ExchangeRuleId(channel.getId(), memberId);

        mockMvc.perform(delete(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchangeRuleId)
                        ))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private void should_update_exchange_rule(Group entity, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ExchangeRuleSpec spec = ExchangeRuleSpec.builder()
                .channelId(channel.getId())
                .memberId(entity.getId())
                .exchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build();

        ResultActions resultActions = mockMvc.perform(put(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spec)
                        ))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_update_the_exchange_rules_of_a_channel",
                    relaxedRequestFields(
                            fieldWithPath(CHANNEL_ID).description(THE_ID_OF_THE_CHANNEL),
                            fieldWithPath("memberId").description("The id of the participant Entity."),
                            fieldWithPath("exchangeMode").description(IF_THE_GROUP_IS_A_SENDER_A_RECIPIENT_OR_BOTH_BIDIRECTIONAL)
                    ),
                    relaxedResponseFields(
                            fieldWithPath("member").description(THE_PARTICIPATING_GROUP),
                            fieldWithPath("exchangeMode").description(IF_THE_GROUP_IS_A_SENDER_A_RECIPIENT_OR_BOTH_BIDIRECTIONAL)
                    )
            ));
        }

        MvcResult result = resultActions.andReturn();

        ExchangeRule exchangeRule = objectMapper.readValue(result.getResponse().getContentAsString(), ExchangeRule.class);

        assertEquals(spec.getMemberId(), exchangeRule.getMember().getId());
        assertEquals(spec.getExchangeMode(), exchangeRule.getExchangeMode());
    }


    private void runUpdateValidationTest(ExchangeRuleSpec spec, SecurityUserDetails userDetails, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(put(UrlTemplates.EXCHANGE_RULES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spec)
                        ))
                .andExpect(status().isBadRequest())
                .andReturn();


        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }
}
