package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.digit.apigw.filter.AccessTokenFilter;
import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.common.features.FeaturesEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

/*
 * For RequestHeaderAuthenticationFilter see https://insource.io/blog/articles/stateless-api-security-with-spring-boot-part-2.html
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
@Configuration
@RequiredArgsConstructor
@SuppressWarnings({"squid:S00112"})
public class SecurityConfig {
    private final CustomPreAuthenticatedUserDetailsService customPreAuthenticatedUserDetailsService;
    private final EtrustexWebProperties etrustexWebProperties;
    private final Environment environment;


    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] antMatchers = new String[] {"/api/**"};

        if (FeaturesEnum.ETRUSTEX_7980.isActive(etrustexWebProperties.getEnvironment())) {
            antMatchers = ArrayUtils.add(antMatchers, "/message-api/**");
        }

        if (etrustexWebProperties.isProdEnvironment() || etrustexWebProperties.isAccEnvironment()) {
            antMatchers = ArrayUtils.add(antMatchers, "/swagger-ui/**");
        }

        http
                .csrf().disable() //no need to check for csrf since cookie based authentication does not work with SessionCreationPolicy. STATELESS
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers()
                    .antMatchers(antMatchers)
                    .and()
                .authorizeRequests().anyRequest().not().anonymous();

        if (Arrays.stream(environment.getActiveProfiles()).noneMatch(s -> s.equals("test"))) {
            http
                    .addFilterBefore(accessTokenFilter(),  AbstractPreAuthenticatedProcessingFilter.class)
                    .addFilterAt(customPreAuthenticatedProcessingFilter(), AbstractPreAuthenticatedProcessingFilter.class);
        }

        http
                .headers()
                // X-frame-options set to SAMEORIGIN to allow streamsaver to work
                .frameOptions().sameOrigin()
                .contentSecurityPolicy("script-src 'self'");

        return http.build();
    }


    /*
     * To avoid "failed hostname verification check. Certificate contained"
     * Console -> Servers -> AdminServer -> Configuration SSL tab -> Advanced -> Change Hostname Verification dropdown to None
     */
    private Filter accessTokenFilter() {
        String clientId = etrustexWebProperties.isDevEnvironment() ? etrustexWebProperties.getClientId() : etrustexWebProperties.getClientOidId();
        String clientSecret = etrustexWebProperties.isDevEnvironment() ? etrustexWebProperties.getClientSecret() : etrustexWebProperties.getClientOidSecret();
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter(clientId, clientSecret, etrustexWebProperties.getEuloginAccessTokenUrlIntrospect());
        accessTokenFilter.whitelist(getWhitelist());

        return accessTokenFilter;
    }

    private Predicate<HttpServletRequest> getWhitelist() {
        final String contextPath = etrustexWebProperties.getContextPath();

        return request ->
                request.getRequestURI().equals(StringUtils.removeEnd(contextPath, "/"))
                        || request.getRequestURI().equalsIgnoreCase(contextPath + "manifest.json")
                        || request.getRequestURI().equalsIgnoreCase(contextPath + "settings/environment")
                        || request.getRequestURI().startsWith(contextPath + "soap")
                        || request.getRequestURI().startsWith(contextPath + "public")
                        || request.getRequestURI().startsWith(contextPath + "img")
                        || request.getRequestURI().equalsIgnoreCase(contextPath + "monitoring");
    }


    private CustomPreAuthenticatedProcessingFilter customPreAuthenticatedProcessingFilter() {
        CustomPreAuthenticatedProcessingFilter filter = new CustomPreAuthenticatedProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    private AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(preAuthenticatedAuthenticationProvider()));
    }

    private PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(customPreAuthenticatedUserDetailsService);
        preAuthenticatedAuthenticationProvider.setThrowExceptionWhenTokenRejected(true);

        return preAuthenticatedAuthenticationProvider;
    }

}
