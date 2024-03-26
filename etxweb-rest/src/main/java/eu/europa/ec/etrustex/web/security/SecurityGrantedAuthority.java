package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;

import java.util.Objects;


public class SecurityGrantedAuthority extends GrantedAuthority implements org.springframework.security.core.GrantedAuthority {

    public SecurityGrantedAuthority(User user, Group group, Role role) {
        super(user, group, role);
    }

    @Override
    public String getAuthority() {
        return super.getRole().getName().toString();
    }

    public boolean isSystemAdmin() {
        return Objects.equals(RoleName.SYS_ADMIN, super.getRole().getName());
    }

    public boolean hasAuthority(RoleName roleName, Long groupId) {
        return Objects.equals(super.getRole().getName(), roleName) && Objects.equals(super.getGroup().getId(), groupId);
    }
}
