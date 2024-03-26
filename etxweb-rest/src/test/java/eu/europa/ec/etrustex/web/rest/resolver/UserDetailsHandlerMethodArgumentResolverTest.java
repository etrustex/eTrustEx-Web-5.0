/*
 * Copyright (c) 2018-2024. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.rest.resolver;

import eu.europa.ec.etrustex.web.exchange.model.UserDetails;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* Suppress method names false positive. */
class UserDetailsHandlerMethodArgumentResolverTest {
    @Mock
    private MethodParameter parameter;

    @Mock
    private ModelAndViewContainer mavContainer;
    @Mock
    private NativeWebRequest webRequest;
    @Mock
    private WebDataBinderFactory binderFactory;
    @Mock
    private Authentication authentication;

    private final UserDetailsHandlerMethodArgumentResolver userDetailsHandlerMethodArgumentResolver = new UserDetailsHandlerMethodArgumentResolver();

    @BeforeEach
    void inti() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_support_SecurityUserDetails_parameter() {
        doReturn(SecurityUserDetails.class).when(parameter).getParameterType();

        assertTrue(userDetailsHandlerMethodArgumentResolver.supportsParameter(parameter));
    }

    @Test
    void should_not_support_not_SecurityUserDetails_parameter() {
        doReturn(UserDetails.class).when(parameter).getParameterType();

        assertFalse(userDetailsHandlerMethodArgumentResolver.supportsParameter(parameter));
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = "USER")
    void should_resolve_SecurityUserDetails_argument() {
        SecurityUserDetails securityUserDetails = new SecurityUserDetails();

        given(authentication.getPrincipal()).willReturn(securityUserDetails);

        Object argument = userDetailsHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertNotNull(argument);
        assertTrue(argument.getClass().isAssignableFrom(SecurityUserDetails.class));
    }

    @Test
    void should_throw_if_not_resolve_SecurityUserDetails_argument() {
        SecurityContextHolder.getContext().setAuthentication(null);
        Exception exception = assertThrows(AccessDeniedException.class, () -> userDetailsHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));

        assertTrue(exception.getMessage().contains("No Authentication for context path "));
    }
}