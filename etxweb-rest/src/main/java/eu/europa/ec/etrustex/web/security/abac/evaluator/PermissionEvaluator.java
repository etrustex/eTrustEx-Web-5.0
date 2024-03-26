package eu.europa.ec.etrustex.web.security.abac.evaluator;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.PolicyDefinition;

/**
 * A permission evaluator which can check if a {@link SecurityUserDetails}
 * against {@link PolicyDefinition} rules
 * if an action can be performed.
 *
 * @author guerrpa
 */
public interface PermissionEvaluator {
    /**
     * Determines if access is granted for a specific authentication and object.
     *
     * @param userDetails the SecurityUserDetails to check
     * @param domainType the domain type for which permission is checked
     * @param action the {@link UserAction}
     * @return true if authorization is granted.
     */
    boolean hasPermission(SecurityUserDetails userDetails, Class<?> domainType, UserAction action);

    /**
     * Determines if access is granted for a specific authentication and object id.
     *
     * @param userDetails the SecurityUserDetails to check
     * @param targetId the target Domain Object Id to check as a Long
     * @param domainType the domain type for which permission is checked
     * @param action the {@link UserAction}
     * @return true if authorization is granted.
     */
    boolean hasPermission(SecurityUserDetails userDetails, Long targetId, Class<?> domainType, UserAction action);

    /**
     * Determines if access is granted for a specific authentication and object id.
     *
     * @param userDetails the SecurityUserDetails to check
     * @param targetId the target Domain Object Id to check as a String
     * @param domainType the domain type for which permission is checked
     * @param action the {@link UserAction}
     * @return true if authorization is granted.
     */
    boolean hasPermission(SecurityUserDetails userDetails, String targetId, Class<?> domainType, UserAction action);

    /**
     * Determines if access is granted for a specific authentication and object.
     *
     * @param userDetails the SecurityUserDetails to check
     * @param spec the object specification to check
     * @param targetId the target Domain Object Id check
     * @param domainType the domain type for which permission is checked
     * @param action the {@link UserAction}
     * @return true if authorization is granted.
     */
    boolean hasPermission(SecurityUserDetails userDetails, Object spec, Long targetId, Class<?> domainType, UserAction action);

    /**
     * Determines if access is granted for a specific authentication and object.
     *
     * @param userDetails the SecurityUserDetails to check
     * @param spec the object specification to check
     * @param domainType the domain type for which permission is checked
     * @param action the {@link UserAction}
     * @return true if authorization is granted.
     */
    boolean hasPermission(SecurityUserDetails userDetails, Object spec, Class<?> domainType, UserAction action);
}
