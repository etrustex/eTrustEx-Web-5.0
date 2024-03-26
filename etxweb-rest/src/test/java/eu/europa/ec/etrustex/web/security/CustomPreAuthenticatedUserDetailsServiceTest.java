package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.UserRegistrationRequestRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGrantedAuthorities;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static eu.europa.ec.etrustex.web.security.CustomPreAuthenticatedUserDetailsService.USER_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomPreAuthenticatedUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Mock
    private UserRegistrationRedirectRepository registrationRedirectRepository;
    @Mock
    private UserRegistrationRequestRepository userRegistrationRequestRepository;
    private CustomPreAuthenticatedUserDetailsService customPreAuthenticatedUserDetailsService;
    private User user;

    @BeforeEach
    public void setUp() {
        this.customPreAuthenticatedUserDetailsService = new CustomPreAuthenticatedUserDetailsService(this.userRepository,
                this.grantedAuthorityRepository, registrationRedirectRepository, userRegistrationRequestRepository);
        this.user = mockUser();
    }


    @Test
    void should_load_user_details() {
        given(userRepository.findByEcasIdIgnoreCase(anyString())).willReturn(Optional.of(user));
        given(grantedAuthorityRepository.findByUser(user)).willReturn(mockGrantedAuthorities(user));

        PreAuthenticatedAuthenticationToken token = SecurityTestUtils.mockAuthentication(user.getEcasId());
        UserDetails userDetails = customPreAuthenticatedUserDetailsService.loadUserDetails(token);

        assertThat(userDetails).isInstanceOf(SecurityUserDetails.class);

        SecurityUserDetails securityUserDetails = (SecurityUserDetails) userDetails;

        assertEquals(securityUserDetails.getUsername(), user.getEcasId());
        assertEquals(securityUserDetails.getAuthorities(), mockGrantedAuthorities(user));
    }

    @Test
    void should_load_user_details_with_granted_authorities() {
        List<GrantedAuthority> grantedAuthorities = mockGrantedAuthorities(user);

        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(grantedAuthorityRepository.findByUser(user)).willReturn(grantedAuthorities);

        PreAuthenticatedAuthenticationToken token = SecurityTestUtils.mockAuthentication(user.getEcasId());
        UserDetails userDetails = customPreAuthenticatedUserDetailsService.loadUserDetails(token);
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) userDetails;

        assertEquals(user.getEcasId(), securityUserDetails.getUsername());
        assertEquals(grantedAuthorities, securityUserDetails.getAuthorities());
    }

    @Test
    void should_throw_UNFE_when_the_principal_is_null() {
        PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(null, null);
        UsernameNotFoundException unfe = assertThrows(
                UsernameNotFoundException.class,
                () -> customPreAuthenticatedUserDetailsService.loadUserDetails(preAuthenticatedAuthenticationToken));

        assertThat(unfe).hasMessage("Principal cannot be null.");
    }

    @Test
    void should_throw_UNFE_when_the_user_is_not_found() {
        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.empty());

        PreAuthenticatedAuthenticationToken principal = SecurityTestUtils.mockAuthentication(user.getEcasId());

        UsernameNotFoundException unfe = assertThrows(
                UsernameNotFoundException.class,
                () -> customPreAuthenticatedUserDetailsService.loadUserDetails(principal));

        assertThat(unfe).hasMessage(USER_NOT_FOUND_MSG + principal.getName());
    }

    @Test
    void should_throw_UNFE_when_the_user_is_not_member_of_group() {
        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(grantedAuthorityRepository.findByUser(any())).willReturn(Collections.emptyList());

        PreAuthenticatedAuthenticationToken principal = SecurityTestUtils.mockAuthentication(user.getEcasId());

        UsernameNotFoundException unfe = assertThrows(
                UsernameNotFoundException.class,
                () -> customPreAuthenticatedUserDetailsService.loadUserDetails(principal));

        assertThat(unfe).hasMessage(USER_NOT_FOUND_MSG + user.getEcasId());
    }

    @Test
    void should_throw_UNFE_when_the_user_has_no_granted_authorities() {
        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(grantedAuthorityRepository.findByUser(any())).willReturn(Collections.emptyList());

        PreAuthenticatedAuthenticationToken principal = SecurityTestUtils.mockAuthentication(user.getEcasId());

        UsernameNotFoundException unfe = assertThrows(
                UsernameNotFoundException.class,
                () -> customPreAuthenticatedUserDetailsService.loadUserDetails(principal));

        assertThat(unfe).hasMessage(USER_NOT_FOUND_MSG + user.getEcasId());
    }
}
