package eu.europa.ec.etrustex.web.security.abac.enforcement.context;

import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Builder
@Getter
@ToString
public class PolicyRuleContext {
    private final SecurityUserDetails principal;
    private final String action;
    private final Object target;
    private final Map<String, Object> environment;
}
