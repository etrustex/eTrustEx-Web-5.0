package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;

public class GroupSpecTestUtils {

    private GroupSpecTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static GroupSpec mockGroupSpec(String groupIdentifier) {
        return mockGroupSpec(1L, groupIdentifier, "Group " + groupIdentifier);
    }

    public static GroupSpec mockGroupSpec(Long parentId, String groupIdentifier, String groupName) {
        return GroupSpec.builder()
                .parentGroupId(parentId)
                .identifier(groupIdentifier)
                .displayName(groupName)
                .build();
    }

    public static GroupSpec mockGroupSpec(Long parentId, String groupIdentifier, String groupName, String description) {
        return GroupSpec.builder()
                .parentGroupId(parentId)
                .identifier(groupIdentifier)
                .displayName(groupName)
                .description(description)
                .isActive(true)
                .newMessageNotificationEmailAddresses("one@email.com,two@email.com")
                .registrationRequestNotificationEmailAddresses("one@email.com,two@email.com")
                .statusNotificationEmailAddress("")
                .build();
    }

    public static GroupSpec mockGroupSpec(Group group) {
        return GroupSpec.builder()
                .id(group.getId())
                .identifier(group.getIdentifier())
                .parentGroupId(group.getBusinessId())
                .displayName(group.getName())
                .description(group.getDescription())
                .isActive(group.isActive())
                .build();
    }
}
