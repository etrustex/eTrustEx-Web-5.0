package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;

public interface BasicPolicyEnforcement {

    boolean check(SecurityUserDetails userDetails, String type, UserAction action);
}
