package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;

public interface RoleService {
    Role getRole(RoleName roleName);
}
