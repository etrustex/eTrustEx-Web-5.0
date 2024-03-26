package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.service.validation.model.QuickStartSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"})
class QuickStartControllerTest extends AbstractControllerTest {

    @Test
    void integrationTest() throws Exception {
        happyFlows();
    }

    private void happyFlows() throws Exception {
        should_create();
    }

    void should_create() throws Exception {
        String newUserEcasId = RandomStringUtils.random(7, true, false).toLowerCase();
        QuickStartSpec quickStartSpec = QuickStartSpec.builder()
                .groupSpec(GroupSpec.builder()
                        .type(ENTITY)
                        .parentGroupId(abstractTestBusiness.getId())
                        .identifier("entityId_" + RANDOM.nextLong())
                        .displayName("entity_name_" + RANDOM.nextLong())
                        .description("The description of the group")
                        .isActive(true)
                        .build())
                .channelSpec(ChannelSpec.builder()
                        .businessId(abstractTestBusiness.getId())
                        .description("The description of the channel")
                        .isActive(true)
                        .defaultChannel(true)
                        .defaultExchangeMode(ExchangeMode.BIDIRECTIONAL)
                        .name("channel_" + RANDOM.nextLong())
                        .build())
                .existingUsers(Collections.singletonList(UserProfileSpec.builder()
                        .ecasId(abstractTestUser.getEcasId())
                        .name(abstractTestUser.getEcasId())
                        .build()))
                .newUsers(Collections.singletonList(UserProfileSpec.builder()
                        .ecasId(newUserEcasId)
                        .name(newUserEcasId)
                        .build()))
                .roleNames(Collections.singletonList(RoleName.OPERATOR))
                .newMessageNotification(true)
                .build();

        mockMvc.perform(post(UrlTemplates.QUICK_START)
                        .with(user(sysAdminUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quickStartSpec)))
                .andExpect(status().isCreated())
                .andReturn();

        assertFalse(channelRepository.findByBusinessIdAndDefaultChannelIsTrue(abstractTestBusiness.getBusinessId()).isEmpty());
    }
}
