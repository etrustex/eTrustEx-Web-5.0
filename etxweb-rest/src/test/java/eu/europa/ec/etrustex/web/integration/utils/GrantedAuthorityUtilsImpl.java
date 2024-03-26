package eu.europa.ec.etrustex.web.integration.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@Component
public class GrantedAuthorityUtilsImpl implements GrantedAuthorityUtils {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Override
    public void create(String username, Long groupId, RoleName role, SecurityUserDetails userDetails) {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(groupId)
                .userName(username)
                .roleName(role)
                .build();

        try {
            mockMvc.perform(
                            post(UrlTemplates.GRANTED_AUTHORITIES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                    .with(user(userDetails)))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public void delete(String username, Long groupId, RoleName role, SecurityUserDetails userDetails) {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(groupId)
                .userName(username)
                .roleName(role)
                .build();

        try {
            mockMvc.perform(
                            RestDocumentationRequestBuilders.delete(UrlTemplates.GRANTED_AUTHORITIES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }

    }
}
