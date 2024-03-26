/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.validation;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public enum ValidationMessage {
    USER_ALREADY_SYS_ADMIN(Constants.USER_ALREADY_SYS_ADMIN_VALUE),
    USER_ALREADY_OFFICIAL_IN_CHARGE(Constants.USER_ALREADY_OFFICIAL_IN_CHARGE_VALUE);

    private final String message;

    public static String formatUniqueGrantedAuthorityMessage(GroupType groupType, RoleName roleName) {
        return String.format(Constants.USER_ALREADY_HAS_GRANTED_AUTHORITY_VALUE, roleName, StringUtils.capitalize(groupType.toString().toLowerCase()));
    }

    public static String formatUserExistsInGroupMessage(GroupType groupType) {
        return String.format(Constants.USER_EXISTS_IN_GROUP_VALUE, StringUtils.capitalize(groupType.toString()));
    }

    public static String formatGroupDoesNotExistMessage(Long groupId) {
        return String.format(Constants.GROUP_DOES_NOT_EXIST_ERROR_MSG, groupId);
    }

    public static String formatForbiddenExtensionsMessage(Collection<String> forbiddenExtensions) {
        return String.format(Constants.FILE_EXTENSION_FORBIDDEN, String.join(", ", forbiddenExtensions));
    }

    public static String formatWindowsCompatibilityErrorMessage(String forbiddenName) {
        return String.format(Constants.FILE_NAME_NOT_WINDOWS_COMPATIBLE, forbiddenName);
    }

    public static String formatExchangeRuleExistsForChannelAndMemberErrorMessage(Long channelId, Long memberId) {
        return String.format(Constants.NON_EXISTING_EXCHANGE_RULE_FOR_CHANNEL_AND_MEMBER_ERROR_MSG, channelId, memberId);
    }

    public static class Constants {
        public static final String ALLOWED_CONFIDENTIALITY_VALUES_MSG = "Allowed values for Confidentiality are PUBLIC and LIMITED_HIGH.";
        public static final String FILE_EXTENSION_FORBIDDEN = "Some attachment has a forbidden extension. Forbidden extensions are: %s.";
        public static final String FILE_NAME_NOT_WINDOWS_COMPATIBLE = "Some attachment has a name of path incompatible with Windows. File or folder %s has an incompatible name, contains leading or trailing spaces, or a forbidden character.";
        public static final String BUSINESS_DISPLAY_NAME_UNIQUE = "The Business display name should be unique.";
        public static final String BUSINESS_CANNOT_HAVE_NEW_MESSAGE_NOTIFICATION_EMAILS = "A Business cannot have any new message notification email configured.";
        public static final String BUSINESS_CANNOT_HAVE_REGISTRATION_REQUEST_NOTIFICATION_EMAILS = "A Business cannot have any registration request notification email configured.";
        public static final String BUSINESS_CANNOT_HAVE_A_STATUS_NOTIFICATION_EMAIL = "A Business cannot have any status notification email configured.";
        public static final String BUSINESS_CANNOT_HAVE_RECIPIENT_PREFERENCES = "A Business cannot have recipient preferences configured.";
        public static final String BUSINESS_NOT_NULL_ERROR_MSG = "Business should not be null.";
        public static final String CONFIDENTIALITY_NOT_NULL_MSG = "Confidentiality should not be null.";
        public static final String CONTENT_NOT_NULL_ERROR_MSG = "The alert content cannot be empty.";
        public static final String DESCRIPTION_LENGTH_ERROR_MSG = "The Description should be between 1 and 255 characters long.";
        public static final String DISPLAY_NAME_LENGTH_ERROR_MSG = "Display Name should be between 1 and 100 characters long.";
        public static final String DISPLAY_NAME_TRIM_ERROR_MSG = "Display Name cannot contain leading or trailing whitespaces.";
        public static final String EMAIL_ADDRESS_LENGTH_ERROR_MSG = "Email Address should be between 1 and 255 characters long.";
        public static final String EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG = "The list of email should be comma separated, and it should contain at most 10 valid addresses.";
        public static final String EMAIL_ADDRESS_NOT_VALID_ERROR_MSG = "Email Address should be valid.";
        public static final String EMAIL_ADDRESS_TRIM_ERROR_MSG = "Email Address cannot contain leading or trailing whitespaces.";
        public static final String END_DATE_CANNOT_PRECEDE_START_DATE_ERROR_MSG = "The end date must be later than the start date.";
        public static final String ENTITY_DISPLAY_NAME_UNIQUE_IN_BUSINESS = "The Group display name should be unique within a business.";
        public static final String ENTITY_HAS_USERS_OR_MESSAGES_ERROR_MSG = "You are not allowed to remove an entity that has messages.";
        public static final String GROUP_IS_NOT_A_BUSINESS = "You are not allowed to perform this action.";
        public static final String EU_LOGIN_ID_LENGTH_ERROR_MSG = "Eu Login ID should be between 1 and 50 characters long.";
        public static final String EU_LOGIN_ID_NOT_NULL_ERROR_MSG = "Eu Login ID should not be null.";
        public static final String EU_LOGIN_ID_TRIM_ERROR_MSG = "Eu Login ID cannot contain leading or trailing whitespaces.";
        public static final String FILE_NAME_REPEATED_ERROR_MSG = "Sending multiple files with the same path and name is not allowed.";
        public static final String NOT_EMPTY_ATTACHMENTS = "Add at least 1 attachment.";
        public static final String NOT_EMPTY_RECIPIENTS = "Select at least one recipient";
        public static final String FORBIDDEN_EXTENSIONS_MAX_LENGTH_ERROR_MSG = "The list of forbidden extensions cannot exceed 4000 characters (excluding spaces).";
        public static final String FORBIDDEN_EXTENSIONS_NO_REPETITIONS = "The list of forbidden extensions cannot contain repeated extensions.";
        public static final String FORBIDDEN_EXTENSIONS_FORMAT_ERROR_MSG = "The list of forbidden extensions should be comma separated and contain no space. Each extension should only contain capital letters and numbers.";
        public static final String GROUP_DOES_NOT_EXIST_ERROR_MSG = "Group with name %s does not exist.";
        public static final String GROUP_IDENTIFIER_LENGTH_ERROR_MSG = "Identifier should be between 1 and 255 characters long.";
        public static final String GROUP_ID_NOT_NULL_ERROR_MSG = "Identifier should not be null.";
        public static final String GROUP_IDENTIFIER_TRIM_ERROR_MSG = "Identifier cannot contain leading or trailing whitespaces.";
        public static final String GROUP_IDENTIFIER_VALID_ERROR_MSG = "The Name can only contain letters, digits, hyphens and underscores.";
        public static final String INVALID_PUBLIC_KEY_MSG = "The public key is not valid.";
        public static final String PUBLIC_KEY_NOT_NULL_MSG = "The public key should not be empty.";
        public static final String NAME_LENGTH_ERROR_MSG = "Name should be between 1 and 255 characters long.";
        public static final String NAME_NOT_NULL_ERROR_MSG = "Name should not be null.";
        public static final String NAME_TRIM_ERROR_MSG = "Name cannot contain leading or trailing whitespaces.";
        public static final String NAME_UNIQUE_ERROR_MSG = "Name already exists.";
        public static final String NAME_VALID_ERROR_MSG = "The Name can only contain letters, digits, hyphens and underscores.";
        public static final String NOTIFICATION_PREFERENCES_WITHOUT_EMAIL_ERROR_MSG = "Notifications cannot be set to true without an email address.";
        public static final String OPTIONAL_DESCRIPTION_LENGTH_ERROR_MSG = "Description should be at most 255 characters long.";
        public static final String RECIPIENT_PREFERENCES_ID_DOES_NOT_EXIST_MSG = "RecipientPreferences id does not exist.";
        public static final String START_DATE_IS_REQUIRED_ERROR_MSG = "The start date is required.";
        public static final String STATUS_NOT_NULL_ERROR_MSG = "Status should not be null.";
        public static final String TITLE_NOT_NULL_ERROR_MSG = "Title should not be null.";
        public static final String TITLE_LENGTH_ERROR_MSG = "Title should be between 1 and 255 characters long.";
        public static final String PASSWORD_NOT_NULL_ERROR_MSG = "Password should not be null.";
        public static final String TYPE_NOT_NULL_ERROR_MSG = "Type should not be null.";
        public static final String UNIQUE_GROUP_NAME_ERROR_MSG = "Display Name already exists.";
        public static final String UNIQUE_GROUP_IDENTIFIER_ERROR_MSG = "Identifier already exists.";
        public static final String USER_ALREADY_HAS_GRANTED_AUTHORITY_VALUE = "User already has %1$s in this %2$s.";
        public static final String USER_ALREADY_SYS_ADMIN_VALUE = "User already is System Administrator.";
        public static final String USER_ALREADY_OFFICIAL_IN_CHARGE_VALUE = "User already is Official in Charge.";
        public static final String USER_EXISTS_IN_GROUP_VALUE = "User already exists in this %s.";
        public static final String OPERATOR_ROLE_BLANK_ERROR_MSG = "Operator role cannot be empty";
        public static final String ADMIN_ROLE_BLANK_ERROR_MSG = "Administrator role cannot be empty";
        public static final String SPLASH_SCREEN_CONTENT_EMPTY_ERROR_MSG = "The splash screen content is required";
        public static final String NON_EXISTING_EXCHANGE_RULE_FOR_CHANNEL_AND_MEMBER_ERROR_MSG = "Non existing Exchange rule with channel: %1$s and member: %2$s.";
        public static final String BCC_OR_CC_EMAIL_IS_REQUIRED_ERROR_MSG = "At least one email address should be added to BCC or CC field";
        public static final String RETENTION_POLICY_MAX_LENGTH_ERROR_MSG = "The value cannot be greater than 7300 days (~20 years)";
        public static final String GROUP_ID_NOT_EMPTY_ERROR_MSG = "The group id cannot be empty.";
        public static final String REQUIRED_GROUP = "The group cannot be empty.";
        public static final String REQUIRED_CHANNEL = "The channel cannot be empty.";
        public static final String USER_ALREADY_CONFIGURED_ERROR_MSG = "This user is already configured for %s.";
        public static final String USER_ALREADY_SENT_REQUEST_ERROR_MSG = "This user has already a pending request for %s.";
        public static final String USER_NOT_ALLOWED_TO_USE_THIS_PAGE_ERROR_MSG = "You are not allowed to use this form to send a User Registration Request";
        public static final String ENTITY_NOT_EMPTY_ERROR_MSG = "The entity cannot be empty.";
        public static final String MESSAGE_GROUP_REFERENCE_ERROR_MSG = "The message should provide a way to find the recipient entity (recipientId | entitySpec).";
        public static final String USER_IS_NOT_CONFIGURED_IN_GROUP_VALUE = "User is not configured";

        public static final String BUSINESS_IDENTIFIER_REQUIRED_MESSAGE = "Business Identifier is required";
        public static final String ENTITY_IDENTIFIER_REQUIRED_MESSAGE = "Entity Identifier is required";
        public static final String TARGET_DIR_REQUIRED_MESSAGE = "File Download directory is required";
        public static final String PRIVATE_KEY_REQUIRED_MESSAGE = "Private Key is needed for decryption";
        public static final String RECIPIENT_REQUIRED_MESSAGE = "At least 1 recipient must be specified";
        public static final String SOURCE_DIR_REQUIRED_MESSAGE = "File Upload directory is required";
        public static final String SUBJECT_REQUIRED_MESSAGE = "Message Subject is required";
        public static final String SUBJECT_TRIM_MESSAGE = "Subject cannot contain leading or trailing whitespaces.";
        public static final String SUBJECT_LENGTH_ERROR_MESSAGE = "Subject should be at least 1 character long.";
        public static final String SUBJECT_MAX_BYTE_LENGTH_ERROR_MESSAGE = "Subject should be at most {value} bytes long.";
        public static final String MISSING_CLIENT_PUBLIC_KEY_HEADER_MESSAGE = "CLIENT-PUBLIC-KEY request header is required";

        private Constants() {
        }
    }

}
