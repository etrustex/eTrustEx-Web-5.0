package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.validation.model.DeleteUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.*;

@Service
@RequiredArgsConstructor
public class UserProfilePolicyEnforcement implements PolicyEnforcement {

    private static final String ENVIRONMENT_ROLE_KEY = "role";
    private static final String BUSINESS_ID = "businessId";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_TYPE = "groupType";
    private final PolicyChecker policyChecker;
    private final GroupRepository groupRepository;
    private final GrantedAuthorityRepository grantedAuthorityRepository;

    @Override
    public String getDomainType() {
        return UserProfile.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {

        if (target instanceof UserProfileSpec) {
            return check(userDetails, (UserProfileSpec) target, action);
        }

        if (target instanceof DeleteUserProfileSpec) {
            return check(userDetails, (DeleteUserProfileSpec) target, action);
        }

        throw new EtxWebException("Target is not a UserProfileSpec, UserProfileSpec or a DeleteUserProfileSpec");
    }


    @Override
    public boolean check(SecurityUserDetails userDetails, Long groupId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        groupRepository.findById(groupId).ifPresent(group -> {
            environment.put(BUSINESS_ID, group.getBusinessId());
            environment.put(GROUP_ID, group.getId());
        });

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    private boolean check(SecurityUserDetails userDetails, UserProfileSpec userProfileSpec, UserAction action) {
        Map<String, Object> environment = new HashMap<>();
        Long groupId = userProfileSpec.getGroupId();

        environment.put(GROUP_ID, groupId);

        groupRepository.findById(userProfileSpec.getGroupId())
                .ifPresent(group -> environment.put(BUSINESS_ID, group.getBusinessId()));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    private boolean check(SecurityUserDetails userDetails, DeleteUserProfileSpec deleteUserProfileSpec, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        grantedAuthorityRepository.findByUserEcasIdAndGroupId(deleteUserProfileSpec.getEcasId(), deleteUserProfileSpec.getGroupId())
                .forEach(grantedAuthority -> addRoleToEnvironment(grantedAuthority.getRole().getName(), environment));

        groupRepository.findById(deleteUserProfileSpec.getGroupId())
                .ifPresent(group -> {
                    environment.put(BUSINESS_ID, group.getBusinessId());
                    environment.put(GROUP_TYPE, group.getType());
                });

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(deleteUserProfileSpec)
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    private void addRoleToEnvironment(RoleName roleName, Map<String, Object> environment) {

        switch (roleName) {
            case OPERATOR:
                environment.computeIfAbsent(ENVIRONMENT_ROLE_KEY, k -> environment.put(k, OPERATOR));
                break;
            case GROUP_ADMIN:
                if (!Objects.equals(SYS_ADMIN, environment.get(ENVIRONMENT_ROLE_KEY))) {
                    environment.put(ENVIRONMENT_ROLE_KEY, GROUP_ADMIN);
                }
                break;
            case SYS_ADMIN:
                environment.put(ENVIRONMENT_ROLE_KEY, SYS_ADMIN);
                break;
            case OFFICIAL_IN_CHARGE:
                environment.put(ENVIRONMENT_ROLE_KEY, OFFICIAL_IN_CHARGE);
                break;
        }
    }
}
