package eu.europa.ec.etrustex.web.integration.utils;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.springframework.test.web.servlet.MockMvc;

public interface UserUtils {
    UserProfile createUserProfile(String username, Long gorupId, SecurityUserDetails userDetails, MockMvc mockMvc);

    UserProfile createUserProfile(String username, Long groupId, SecurityUserDetails userDetails, MockMvc mockMvc, boolean withNotifications);

    SecurityUserDetails buildUserDetails(User user, Group group, RoleName roleName);
}
