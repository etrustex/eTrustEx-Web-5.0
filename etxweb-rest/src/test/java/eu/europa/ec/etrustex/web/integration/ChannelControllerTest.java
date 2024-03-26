package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.SENDER;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChannelControllerTest extends AbstractControllerTest {
    private static final String CHANNEL_DESCRIPTION = "channel description";
    private static final String BUSINESS_ID_PARAM = "businessId";
    private static final String ENTITY_ID_PARAM = "entityId";
    private static final String SEARCH_STRING_PARAM = "name";

    private final ResponseFieldsSnippet channelResponseFields = relaxedResponseFields(
            fieldWithPath("id").description("The channel id"),
            fieldWithPath("name").description("The channel name (unique for each business)"),
            fieldWithPath("business").description("The business that owns the channel"),
            fieldWithPath("description").description("The channel description")
    );

    private final ResponseFieldsSnippet channelsResponseFields = relaxedResponseFields(
            fieldWithPath("content[].id").description("The channel id"),
            fieldWithPath("content[].name").description("The channel name (unique for each business)"),
            fieldWithPath("content[].business").description("The business that owns the channel"),
            fieldWithPath("content[].description").description("The channel description")
    );

    @Autowired
    private MessageUtils messageUtils;

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
        validationTests();
    }

    private void happyFlows() throws Exception {
        Channel channel1 = should_create_a_channel(abstractTestBusiness.getId(), businessAdminUserDetails, true);
        Channel channel2 = should_create_a_channel(abstractTestBusiness.getId(), sysAdminUserDetails, false);

        should_get_the_channel(channel1.getId(), businessAdminUserDetails, true);
        should_get_the_channel(channel1.getId(), sysAdminUserDetails, false);
        should_get_the_channels_of_the_business(businessAdminUserDetails, true);
        should_get_the_channels_of_the_business(sysAdminUserDetails, false);

        messageUtils.createExchangeRule(channel1.getId(), abstractTestEntity.getId(), SENDER, businessAdminUserDetails);
        should_get_channels_for_entity(abstractTestEntity.getId(), sysAdminUserDetails);

        should_search_for_a_channel("channel", sysAdminUserDetails);

        should_delete_a_channel(channel1.getId(), businessAdminUserDetails, true);
        should_delete_a_channel(channel2.getId(), sysAdminUserDetails, false);
    }

    private void securityAccessTests() throws Exception {
        Group anotherBusiness = groupService.create(GroupSpec.builder()
                .parentGroupId(root.getId())
                .identifier(UUID.randomUUID().toString())
                .displayName(UUID.randomUUID() + " name")
                .type(GroupType.BUSINESS)
                .build());


        Long otherBusinessId = anotherBusiness.getId();

        Channel channel = should_create_a_channel(abstractTestBusiness.getId(), businessAdminUserDetails, false);
        Channel otherBusinessChannel = should_create_a_channel(otherBusinessId, sysAdminUserDetails, false);

        should_not_create_a_channel(otherBusinessId, businessAdminUserDetails);
        should_not_create_a_channel(abstractTestBusiness.getId(), entityAdminUserDetails);
        should_not_create_a_channel(abstractTestBusiness.getId(), operatorUserDetails);

        should_not_get_the_channels_of_business(otherBusinessId, businessAdminUserDetails);
        should_not_get_the_channels_of_business(otherBusinessId, entityAdminUserDetails);
        should_not_get_the_channels_of_business(otherBusinessId, operatorUserDetails);

        should_not_get_the_channels_of_business(abstractTestBusiness.getId(), entityAdminUserDetails);
        should_not_get_the_channels_of_business(abstractTestBusiness.getId(), operatorUserDetails);

        should_not_get_the_channel(channel.getId(), entityAdminUserDetails);
        should_not_get_the_channel(channel.getId(), operatorUserDetails);

        should_not_get_the_channel(otherBusinessChannel.getId(), businessAdminUserDetails);
        should_not_get_the_channel(otherBusinessChannel.getId(), entityAdminUserDetails);
        should_not_get_the_channel(otherBusinessChannel.getId(), operatorUserDetails);

        should_not_delete_a_channel(otherBusinessChannel.getId(), businessAdminUserDetails);
        should_not_delete_a_channel(otherBusinessChannel.getId(), entityAdminUserDetails);
        should_not_delete_a_channel(otherBusinessChannel.getId(), operatorUserDetails);

    }

    private void validationTests() throws Exception {
        create_channel_validation_should_fail_with_null_fields();
        create_channel_validation_should_fail_with_short_fields();
        create_channel_validation_should_fail_with_long_fields();
        create_channel_validation_should_fail_with_untrimmed_fields();
        Channel channel = should_create_a_channel(abstractTestBusiness.getId(), businessAdminUserDetails, false);
        create_channel_validation_should_fail_with_non_unique_name(abstractTestBusiness.getId(), channel.getName());
    }


    private Channel should_create_a_channel(Long businessId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ChannelSpec channelSpec = ChannelSpec.builder()
                .businessId(businessId)
                .description(CHANNEL_DESCRIPTION)
                .isActive(true)
                .name("channel " + UUID.randomUUID())
                .build();


        ResultActions resultActions = mockMvc.perform(
                        post(UrlTemplates.CHANNELS)
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(channelSpec)
                                ))
                .andExpect(status().isCreated());

        if (withRestDocs) {
            resultActions.andDo(document("should_create_the_channel",
                    relaxedRequestFields(
                            fieldWithPath("name").description("The name of the channel."),
                            fieldWithPath("description").description("The description of the channel."),
                            fieldWithPath(BUSINESS_ID_PARAM).description("The business id of the channel."),
                            fieldWithPath("isActive").description("The status of the channel.")
                    ),
                    channelResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();
        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        assertNotNull(channel);

        return channel;
    }

    private void should_get_the_channel(Long channelId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(UrlTemplates.CHANNEL, channelId)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_get_the_channel",
                    pathParameters(
                            parameterWithName("channelId").description("The id of the channel.")
                    ),
                    channelResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();
        Channel channel = objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);

        assertNotNull(channel);
    }

    private void should_get_the_channels_of_the_business(SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.CHANNELS)
                                .param(BUSINESS_ID_PARAM, "" + abstractTestBusiness.getId())
                                .with(user(userDetails))
                )
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_get_the_channels_of_the_business",
                    relaxedRequestParameters(
                            parameterWithName("page").optional().description("Page to retrieve"),
                            parameterWithName("size").optional().description("Maximum number of items in the response page."),
                            parameterWithName("sortBy").optional().description("Field by which the results must be sorted."),
                            parameterWithName("sortOrder").optional().description("Sorting direction (ASC/DEC)"),
                            parameterWithName("filterValue").optional().description("Filter Value"),
                            parameterWithName(BUSINESS_ID_PARAM).description("The id of the business.")
                    ),
                    channelsResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();
        RestResponsePage<?> channelsPage = objectMapper.readValue(result.getResponse().getContentAsString(), RestResponsePage.class);

        assertThat(channelsPage.getContent().size()).isNotZero();
    }

    private void should_get_channels_for_entity(Long entityId, SecurityUserDetails userDetails) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(UrlTemplates.CHANNELS_BY_GROUP)
                        .param(BUSINESS_ID_PARAM, "" + abstractTestBusiness.getId())
                        .param(ENTITY_ID_PARAM, "" + entityId)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        List<Channel> channels = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(channels).isNotEmpty();
    }

    private void should_search_for_a_channel(String searchString, SecurityUserDetails userDetails) throws Exception {
        ResultActions resultActions = mockMvc.perform(get(UrlTemplates.CHANNELS_SEARCH)
                        .param(BUSINESS_ID_PARAM, "" + abstractTestBusiness.getId())
                        .param(SEARCH_STRING_PARAM, "" + searchString)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        List<SearchItem> channels = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(channels).isNotEmpty();
    }

    private void should_not_create_a_channel(Long businessId, SecurityUserDetails userDetails) throws Exception {
        ChannelSpec channelSpec = ChannelSpec.builder()
                .businessId(businessId)
                .description(CHANNEL_DESCRIPTION)
                .isActive(true)
                .name("channel name")
                .build();

        mockMvc.perform(
                        post(UrlTemplates.CHANNELS)
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(channelSpec)
                                ))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private void should_not_get_the_channels_of_business(Long businessId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.CHANNELS)
                                .param(BUSINESS_ID_PARAM, "" + businessId)
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_the_channel(Long channelId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(get(UrlTemplates.CHANNEL, channelId)
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_delete_a_channel(Long channelId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        delete(UrlTemplates.CHANNEL, channelId)
                                .with(user(userDetails))
                )
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_delete_the_channel",
                    pathParameters(
                            parameterWithName("channelId").description("The id of the channel.")
                    )
            ));
        }

        resultActions.andReturn();
    }

    private void should_not_delete_a_channel(Long channelId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                delete(UrlTemplates.CHANNEL, channelId)
                        .with(user(userDetails)
                        )
        ).andExpect(status().isForbidden());
    }

    private void create_channel_validation_should_fail_with_null_fields() throws Exception {
        runCreateChannelValidationTest(
                ChannelSpec.builder().build(),
                NAME_NOT_NULL_ERROR_MSG,
                BUSINESS_NOT_NULL_ERROR_MSG,
                STATUS_NOT_NULL_ERROR_MSG
        );
    }

    private void create_channel_validation_should_fail_with_short_fields() throws Exception {
        runCreateChannelValidationTest(
                ChannelSpec.builder()
                        .businessId(1L)
                        .name("")
                        .isActive(true)
                        .build(),
                NAME_LENGTH_ERROR_MSG
        );
    }

    private void create_channel_validation_should_fail_with_long_fields() throws Exception {
        runCreateChannelValidationTest(
                ChannelSpec.builder()
                        .businessId(1L)
                        .name(StringUtils.repeat("c", 256))
                        .description(StringUtils.repeat("c", 256))
                        .isActive(true)
                        .build(),
                NAME_LENGTH_ERROR_MSG,
                OPTIONAL_DESCRIPTION_LENGTH_ERROR_MSG
        );
    }

    private void create_channel_validation_should_fail_with_untrimmed_fields() throws Exception {
        runCreateChannelValidationTest(
                ChannelSpec.builder()
                        .businessId(1L)
                        .name(" leading space")
                        .isActive(true)
                        .build(),
                NAME_TRIM_ERROR_MSG
        );
    }

    private void create_channel_validation_should_fail_with_non_unique_name(Long businessId, String name) throws Exception {
        runCreateChannelValidationTest(ChannelSpec.builder()
                .businessId(businessId)
                .description(CHANNEL_DESCRIPTION)
                .isActive(true)
                .name(name)
                .build(), NAME_UNIQUE_ERROR_MSG);
    }

    private void runCreateChannelValidationTest(ChannelSpec channelSpec, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.CHANNELS)
                                .with(user(businessAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(channelSpec)
                                ))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }
}
