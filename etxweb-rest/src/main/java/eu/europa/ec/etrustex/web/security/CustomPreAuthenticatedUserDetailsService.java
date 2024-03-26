package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.UserRegistrationRequestRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomPreAuthenticatedUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    static final String USER_NOT_FOUND_MSG = "User not found: ";
    private final UserRepository userRepository;
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    private final UserRegistrationRedirectRepository registrationRedirectRepository;
    private final UserRegistrationRequestRepository userRegistrationRequestRepository;

    @Override
    @Transactional
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
        log.debug("token.getPrincipal(): " + token.getPrincipal());
        if (token.getPrincipal() == null) {
            throw new UsernameNotFoundException("Principal cannot be null.");
        }

        EtrustexPrincipal principal = (EtrustexPrincipal) token.getPrincipal();
        log.debug("Principal: " + principal.getEuLoginId());

        User user = userRepository.findByEcasIdIgnoreCase(principal.getEuLoginId()).orElse(null);

        if (user == null) {
            List<UserRegistrationRedirect> redirects = registrationRedirectRepository.findByEmailAddress(principal.getEmail());
            if (!CollectionUtils.isEmpty(redirects)) {
                return buildSecurityUserDetails(principal);
            }
            log.warn("User with username {} not configured. The user does not exist", principal.getEuLoginId());
            throw new UsernameNotFoundException(USER_NOT_FOUND_MSG + principal.getEuLoginId());
        }

        Collection<SecurityGrantedAuthority> securityGrantedAuthorities = grantedAuthorityRepository.findByUser(user).stream()
                .filter(GrantedAuthority::isEnabled)
                .map(ga -> new SecurityGrantedAuthority(ga.getUser(), ga.getGroup(), ga.getRole()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(securityGrantedAuthorities)) {
            List<UserRegistrationRedirect> redirects = registrationRedirectRepository.findByEmailAddress(principal.getEmail());
            if (!CollectionUtils.isEmpty(redirects)) {
                UserRegistrationRedirect userRegistrationRedirect = redirects.iterator().next();
                if (userRegistrationRequestRepository.existsByUserEcasIdAndGroupId(principal.getEuLoginId(), userRegistrationRedirect.getGroupId())) {
                    return buildSecurityUserDetails(principal);
                }
            }

            log.warn("User with username {} not configured. The user has no granted authorities", principal.getEuLoginId());
            throw new UsernameNotFoundException(USER_NOT_FOUND_MSG + user.getEcasId());
        }

        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        securityUserDetails.setUser(user);
        securityUserDetails.setAuthorities(securityGrantedAuthorities);
        securityUserDetails.setFirstName(principal.getFirstName());
        securityUserDetails.setLastName(principal.getLastName());
        securityUserDetails.setEmail(principal.getEmail());

        return securityUserDetails;
    }


    private SecurityUserDetails buildSecurityUserDetails(EtrustexPrincipal principal) {
        User user = User.builder()
                        .ecasId(principal.getEuLoginId())
                        .euLoginEmailAddress(principal.getEmail())
                        .name(principal.getLastName() + " " + principal.getFirstName())
                        .build();
        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        securityUserDetails.setUser(user);
        securityUserDetails.setAuthorities(new HashSet<>());
        securityUserDetails.setFirstName(principal.getFirstName());
        securityUserDetails.setLastName(principal.getLastName());
        securityUserDetails.setEmail(principal.getEmail());

        return securityUserDetails;

    }
}
