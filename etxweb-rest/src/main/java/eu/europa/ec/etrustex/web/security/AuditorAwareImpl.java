package eu.europa.ec.etrustex.web.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    public static final String SYSTEM = "System";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(
                Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .map(Principal::getName)
                    .orElse(SYSTEM)
        );
    }
}
