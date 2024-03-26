package eu.europa.ec.etrustex.web.security.abac.enforcement.checker;

import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;

public interface PolicyChecker {
    boolean check(PolicyRuleContext context);
}
