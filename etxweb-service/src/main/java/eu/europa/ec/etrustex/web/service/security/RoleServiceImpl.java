package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.repository.security.RoleRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new EtxWebException(String.format("Role %s not found", roleName)));
    }
}
