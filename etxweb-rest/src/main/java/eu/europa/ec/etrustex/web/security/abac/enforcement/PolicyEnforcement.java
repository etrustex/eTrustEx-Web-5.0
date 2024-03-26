package eu.europa.ec.etrustex.web.security.abac.enforcement;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;

public interface PolicyEnforcement {
	String getDomainType();

	default boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
		return false;
	}

	default boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
		return false;
	}

	default boolean check(SecurityUserDetails userDetails, String targetId, UserAction action) {
		return false;
	}

	default boolean check(SecurityUserDetails userDetails, Object target, Long targetId, UserAction action) {
		return false;
	}
}
