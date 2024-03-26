package eu.europa.ec.etrustex.web.integration.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class GroupUtilsImpl implements GroupUtils {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    public GroupUtilsImpl(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Group createGroup(GroupSpec spec, SecurityUserDetails userDetails) {
        try {
            MvcResult result = mockMvc.perform(
                            post(UrlTemplates.GROUPS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(spec))
                                    .with(user(userDetails)))
                    .andExpect(status().isCreated())
                    .andReturn();

            return objectMapper.readValue(result.getResponse().getContentAsString(), Group.class);
        } catch (Exception e) {
            throw new EtxWebException(e);
        }

    }

    @Override
    public Group createEntity(Long businessId, String entityIdentifier, SecurityUserDetails userDetails) {
        return createEntity(businessId, entityIdentifier, userDetails, false);
    }

    @Override
    public Group createEntity(Long businessId, String entityIdentifier, SecurityUserDetails userDetails, Boolean isSystem) {
        return createGroup(GroupSpec.builder()
                        .parentGroupId(businessId)
                        .identifier(entityIdentifier)
                        .type(GroupType.ENTITY)
                        .displayName(entityIdentifier + " name")
                        .description(entityIdentifier + "description")
                        .newMessageNotificationEmailAddresses(entityIdentifier + "@domain.com")
                        .statusNotificationEmailAddress(entityIdentifier + "@domain.com")
                        .isActive(true)
                        .isSystem(isSystem)
                        .build(),
                userDetails);
    }

    @Override
    public Group createBusiness(Long rootGroupId, String businessIdentifier, SecurityUserDetails userDetails) {
        return createGroup(GroupSpec.builder()
                        .parentGroupId(rootGroupId)
                        .identifier(businessIdentifier)
                        .type(GroupType.BUSINESS)
                        .displayName(businessIdentifier + " name")
                        .description(businessIdentifier + "description")
                        .isActive(true)
                        .build(),
                userDetails);
    }
}
