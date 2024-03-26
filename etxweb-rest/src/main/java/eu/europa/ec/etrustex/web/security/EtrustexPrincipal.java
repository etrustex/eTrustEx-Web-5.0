package eu.europa.ec.etrustex.web.security;

import lombok.Builder;
import lombok.Getter;

import java.security.Principal;

@Getter
@Builder
public final class EtrustexPrincipal implements Principal {
    public static final String CURRENT_GROUP_HEADER_ID = "GROUP-ID";
    private final String euLoginId;
    private final String firstName;
    private final String lastName;
    private final String email;


    @Override
    public String getName() {
        return euLoginId;
    }
}
