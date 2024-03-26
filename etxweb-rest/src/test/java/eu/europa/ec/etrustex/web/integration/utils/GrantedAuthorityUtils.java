package eu.europa.ec.etrustex.web.integration.utils;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;

public interface GrantedAuthorityUtils {
    void create(String username, Long groupId, RoleName role, SecurityUserDetails userDetails);

    void delete(String username, Long groupId, RoleName role, SecurityUserDetails userDetails);
}
