package eu.europa.ec.etrustex.web.integration.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityGrantedAuthority;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.security.RoleService;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RequiredArgsConstructor
@Component
public class UserUtilsImpl implements UserUtils {
    private final ObjectMapper objectMapper;
    private final RoleService roleService;

    @Override
    public UserProfile createUserProfile(String username, Long groupId, SecurityUserDetails userDetails, MockMvc mockMvc) {
        return createUserProfile(username, groupId, userDetails, mockMvc, false);
    }

    @Override
    public UserProfile createUserProfile(String username, Long groupId, SecurityUserDetails userDetails, MockMvc mockMvc, boolean withNotifications) {
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(username)
                .groupId(groupId)
                .alternativeEmailUsed(true)
                .alternativeEmail(username + "@domain.com")
                .name(username + " name")
                .newMessageNotification(withNotifications)
                .statusNotification(withNotifications)
                .build();


        try {
            MvcResult result = mockMvc.perform(
                            post(UrlTemplates.USER_PROFILES)
                                    .with(user(userDetails))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(userProfileSpec))
                    )
                    .andExpect(status().isCreated())
                    .andReturn();
            return objectMapper.readValue(result.getResponse().getContentAsString(), UserProfile.class);
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public SecurityUserDetails buildUserDetails(User user, Group group, RoleName roleName) {
        Role role = roleService.getRole(roleName);
        SecurityGrantedAuthority grantedAuthority = new SecurityGrantedAuthority(user, group, role);

        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        securityUserDetails.setUser(user);
        securityUserDetails.setRoles(Collections.singleton(role));
        securityUserDetails.setAuthorities(Collections.singleton(grantedAuthority));
        securityUserDetails.setFirstName(user.getEcasId() + " firstname");
        securityUserDetails.setLastName(user.getEcasId() + " lastname");
        securityUserDetails.setEmail(user.getEcasId() + "@domain.eu");

        return securityUserDetails;
    }
}
