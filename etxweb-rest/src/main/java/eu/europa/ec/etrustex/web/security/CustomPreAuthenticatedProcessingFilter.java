package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.digit.apigw.filter.AccessToken;
import eu.europa.ec.digit.apigw.filter.AccessTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CustomPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
    @Override
    protected EtrustexPrincipal getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return buildPrincipalObject();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    private EtrustexPrincipal buildPrincipalObject() {
        AccessToken token = AccessTokenFilter.ACCESS_TOKEN.get();
        AccessToken.UserDetails userDetails = token.getUserDetails();
        log.debug("AccessToken.UserDetails: " + userDetails);

        EtrustexPrincipal etrustexPrincipal;

        if (userDetails != null ) {
            etrustexPrincipal = EtrustexPrincipal.builder()
                    .euLoginId(userDetails.getUserId())
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .email(userDetails.getEmail())
                    .build();
        } else {
            etrustexPrincipal = null;
        }


        return etrustexPrincipal;
    }
}
