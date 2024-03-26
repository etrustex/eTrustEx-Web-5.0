package eu.europa.ec.etrustex.web.security.abac.evaluator;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcementFactory;
import eu.europa.ec.etrustex.web.security.abac.enforcement.implementation.BasicPolicyEnforcement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("permissionEvaluator")
@Slf4j
@RequiredArgsConstructor
public class AbacPermissionEvaluator implements PermissionEvaluator {

	private final BasicPolicyEnforcement basicPolicyEnforcement;

	@Override
	public boolean hasPermission(SecurityUserDetails userDetails, Class<?> domainType, UserAction action) {
		return basicPolicyEnforcement.check(userDetails, domainType.getSimpleName(), action);
	}

	@Override
	public boolean hasPermission(SecurityUserDetails userDetails, Long targetId, Class<?> domainType, UserAction action) {
		PolicyEnforcement policyEnforcement = PolicyEnforcementFactory.getPolicyEnforcement(domainType.getSimpleName());
		return policyEnforcement.check(userDetails, targetId, action);
	}

	@Override
	public boolean hasPermission(SecurityUserDetails userDetails, String targetId, Class<?> domainType, UserAction action) {
		PolicyEnforcement policyEnforcement = PolicyEnforcementFactory.getPolicyEnforcement(domainType.getSimpleName());
		return policyEnforcement.check(userDetails, targetId, action);
	}

	@Override
	public boolean hasPermission(SecurityUserDetails userDetails, Object target, Long targetId, Class<?> domainType, UserAction action) {
		PolicyEnforcement policyEnforcement = PolicyEnforcementFactory.getPolicyEnforcement(domainType.getSimpleName());
		return policyEnforcement.check(userDetails, target, targetId, action);
	}

	@Override
	public boolean hasPermission(SecurityUserDetails userDetails, Object target, Class<?> domainType, UserAction action) {
		PolicyEnforcement policyEnforcement = PolicyEnforcementFactory.getPolicyEnforcement(domainType.getSimpleName());
		return policyEnforcement.check(userDetails, target, action);
	}
}
