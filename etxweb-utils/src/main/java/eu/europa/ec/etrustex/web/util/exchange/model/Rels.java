/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Rels {

    SELF("self"),

    ATTACHMENT_CREATE("/attachment/create"),
    ATTACHMENT_GET("/attachment/get"),
    ATTACHMENT_UPLOAD("/attachment/upload"),
    ATTACHMENT_DOWNLOAD("/attachment/download"),
    ATTACHMENT_DELETE("/attachment/delete"),
    ATTACHMENTS_DELETE("/attachments/delete"),

    MESSAGE_GET("/message/get"),
    MESSAGE_CREATE("/message/create"),
    MESSAGE_UPDATE("/message/update"),
    MESSAGE_STATUS("/message/status"),
    MESSAGE_DRAFT("/message/draft"),
    MESSAGE_DELETE("/message/delete"),
    MESSAGE_RECEIPT_GET("/message-receipt/get"),
    MESSAGE_IS_READY_TO_SEND_GET("/is-ready-to-send/message/get"),

    MESSAGES_GET("/messages/get"),
    MESSAGES_COUNT_UNREAD_SENT("/messages/count-unread/get"),
    MESSAGES_MARK_READ("/messages/put"),
    MESSAGES_MONITORING("/message/monitoring"),
    MESSAGES_DISPLAY("/messages/display"),
    MESSAGES_SEARCH("/messages/search"),

    MESSAGE_SUMMARIES_GET("/message-summaries/get"),
    MESSAGE_SUMMARIES_MARK_READ("/message-summaries/put"),
    MESSAGE_SUMMARY_GET("/message-summary/get"),
    MESSAGE_SUMMARIES_COUNT_UNREAD("/message-summaries/count/unread"),
    MESSAGE_SUMMARIES_UPDATE("/message-summaries/update"),
    MESSAGE_SUMMARY_UPDATE_ACTIVE_STATUS("/message-summary/update"),
    MESSAGE_SUMMARY_ACK("/message-summary/ack"),


    USER_DETAILS_GET("/user-details/get"),
    USER_LIST_ITEMS_GET("/user-list-items/get"),
    USER_PROFILE_CREATE("/user-profile/create"),
    USER_PROFILE_EULOGIN("/user-profile/eulogin-info"),
    USER_PROFILE_INFO("/user-profile/info"),
    USER_PROFILES_SEARCH("/users-profile/search"),
    USER_PROFILE_DELETE("/user-profile/delete"),
    USER_PROFILE_UPDATE("/user-profile/update"),
    USER_PROFILE_NOTIFICATION_EMAILS("/user-profile/notification-emails"),
    USER_PROFILE_SEND_NOTIFICATION_EMAILS("/user-profile/send-notification-emails"),
    USER_PROFILE_UPLOAD_BULK("/user-profile/upload-bulk"),
    USER_PREFERENCES_UPDATE("/user-preferences/update"),

    EXPORT_USERS("/export/users"),

    IS_VALID_RECIPIENT("/rules/is-valid-recipient"),
    VALID_RECIPIENTS_GET("/rules/recipients/get"),

    SETTINGS_ENVIRONMENT_GET("/settings/environment/get"),
    SETTINGS_PUBLIC_KEY_GET("/settings/public-key/get"),


    GROUP_CREATE("/group/create"),
    GROUP_GET("/group/get"),
    GROUP_UPDATE("/group/update"),
    IS_VALID_GROUP("/group/is-valid"),
    GROUPS_GET("/groups/get"),
    GROUPS_SEARCH("/groups/search"),
    GROUP_DELETE("/group/delete"),
    BUSINESS_DELETE("/business/delete"),
    IS_GROUP_EMPTY("/group/is-empty"),
    IS_BUSINESS_EMPTY("/group/empty"),
    CANCEL_BUSINESS_DELETE("/group/delete/cancel"),
    CONFIRM_BUSINESS_DELETE("/group/delete/confirm"),
    GROUP_UPLOAD_BULK("/group/upload-bulk"),

    SENDER_EXCHANGE_RULE_GET("/sender-exchange-rule/get"),

    RECIPIENT_PREFERENCES_GET("/recipient-preferences/get"),
    RECIPIENT_PREFERENCES_CREATE("/recipient-preferences/create"),
    RECIPIENT_PREFERENCES_UPDATE("/recipient-preferences/update"),

    TEMPLATES_GET("/templates/get"),

    DEFAULT_TEMPLATES_GET("/default-templates/get"),
    TEMPLATES_UPDATE("/templates/update"),

    DEFAULT_POLICY_CREATE("/default-policy/create"),

    CHANNELS_GET("/channels/get"),
    CHANNEL_GET_BY_GROUP("/group/channels/get"),
    CHANNEL_GET("/channel/get"),
    CHANNEL_SEARCH("/channels/search"),
    CHANNEL_CREATE("/channel/create"),
    CHANNEL_VALIDATE("/channel/validate"),
    CHANNEL_UPDATE("/channel/update"),
    CHANNEL_DELETE("/channel/delete"),
    EXPORT_CHANNELS("/export/channels"),

    EXCHANGE_RULES_GET("/exchange-rules/get"),
    EXCHANGE_RULES_SEARCH("/exchange-rules/search"),
    EXCHANGE_RULES_UPDATE("/exchange-rules/update"),
    EXCHANGE_RULES_BULK_CREATE("/exchange-rules/create/bulk"),
    EXCHANGE_RULES_DELETE("/exchange-rules/delete"),
    EXCHANGE_RULES_UPLOAD_BULK("/exchange-rules/upload-bulk"),
    EXCHANGE_RULES_UPLOAD_PARTICIPANTS_BULK("/exchange-rules/upload-participants-bulk"),
    EXCHANGE_RULES_PARTICIPANTS_UPLOAD_BULK("/exchange-rules-participants/upload-bulk"),
    NOTIFICATION_PREFERENCES_GET("/notification-preferences/get"),
    NOTIFICATION_PREFERENCES_UPDATE("/notification-preferences/update"),

    GRANTED_AUTHORITY_CREATE("/granted-authority/create"),
    GRANTED_AUTHORITY_DELETE("/granted-authority/delete"),
    GRANTED_AUTHORITIES_UPDATE_GROUP("/granted-authority/group"),
    GRANTED_AUTHORITIES_UPDATE_GROUP_BULK("/granted-authority/group/group"),
    GRANTED_AUTHORITIES_CREATE_GROUP_BULK("/granted-authority/group/create"),

    ALERT_GET("/alert/get"),
    ALERT_CREATE("/alert/create"),
    ALERT_UPDATE("/alert/update"),
    ALERT_USER_STATUS_GET("/alert-user-status/get"),
    ALERT_USER_STATUS_CREATE("/alert-user-status/create"),
    ALERT_USER_STATUS_UPDATE("/alert-user-status/update"),

    GROUP_CONFIGURATIONS_GET("/group-configurations/get"),
    GROUP_CONFIGURATION_GET("/group-configuration/get"),
    GROUP_CONFIGURATION_UPDATE("/group-configuration/update"),

    USER_GUIDE_GET("/user-guide/get"),
    USER_GUIDES_GET("/user-guides/get"),
    USER_GUIDE_FILE("/user-guide/file"),
    USER_GUIDE_DELETE("/user-guide/delete"),


    /* SYSTEM ADMINM Rels */
    DELETE_ORPHAN_FILES("/storage/orphan-files/remove"),
    DELETE_GROUP_ORPHAN_FILES("/groups/storage/orphan-files/remove"),

    NEW_CERTIFICATE_JOB("/new-certificate/launch-job"),
    NEW_CERTIFICATE("/new-certificate/job-status"),
    NEW_CERTIFICATE_RESET_UPDATED_FLAG("/new-certificate/reset-updated-flag"),

    UVSCAN_TEST("/uvscan/test"),

    TESTS_CLEAN_UP_DELETE("/tests-clean-up/delete"),

    INTEGRATION_TEST_SUBMIT_DOCUMENT("/integration-test/document-bundle/submit"),

    ROOT_GROUP_GET("/group/admin/get"),
    GROUPS_ADMIN_GET("/groups/admin/get"),
    GROUPS_ADMIN_SEARCH("/groups/admin/search"),
    SYS_ADMIN_LIST_ITEMS_GET("/sys-admin-list-items/get"),
    SYS_ADMIN_PROFILE_CREATE("/sys-admin-profile/create"),
    SYS_ADMIN_PROFILES_SEARCH("/sys-admins-profile/search"),
    SYS_ADMIN_PROFILE_DELETE("/sys-admin-profile/delete"),
    SYS_ADMIN_PROFILE_UPDATE("/sys-admin-profile/update"),

    USER_REGISTRATION_REQUEST_CREATE("/user-registration-request/create"),
    USER_REGISTRATION_REQUEST_DELETE("/user-registration-request/delete"),
    USER_REGISTRATION_REQUEST_UPDATE("/user-registration-request/update"),
    MESSAGE_SUMMARY_DISABLE("/message-summary/disable"),
    CERTIFICATE_UPDATE("/certificate-update/generate-link"),
    CERTIFICATE_UPDATE_COMPROMISED_MESSAGES("/certificate-update/update-compromised-messages"),
    CERTIFICATE_UPDATE_IS_VALID_REDIRECT_LINK("/certificate-update/is-valid-redirect-link"),
    COUNT_MESSAGE_SUMMARIES_TO_DISABLE("/message-summary/count/disable"),
    LOG_LEVEL_SEARCH("/log-level/search"),
    LOG_LEVEL_UPDATE("/log-level/update"),
    LOG_LEVEL_RESET("/log-level/reset"),
    QUICK_START("/quick-start")
    ;


    private final String rel;

    Rels(String rel) {
        this.rel = rel;
    }

    public static Rels getValue(String rel) {
        for (Rels r : Rels.values()) {
            if (r.toString().equalsIgnoreCase(rel)) {
                return r;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.rel;
    }
}
