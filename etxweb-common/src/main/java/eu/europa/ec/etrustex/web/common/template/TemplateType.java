package eu.europa.ec.etrustex.web.common.template;

public enum TemplateType {
    MESSAGE_DETAILS_VIEW("message-details"),
    MESSAGE_SUMMARY_VIEW("message-summary"),
    NEW_MESSAGE_FORM("new-message-notification"),
    MESSAGE_STATUS_NOTIFICATION("message-status-notification"),
    NEW_MESSAGE_NOTIFICATION("new-message-notification"),
    SENT_MESSAGE_DETAILS_VIEW("sent-message-details-view"),
    RETENTION_POLICY_NOTIFICATION("retention-policy-email-notification"),
    UNREAD_MESSAGE_REMINDER_NOTIFICATION("unread-message-reminder-email-notification"),
    USER_CONFIGURED_NOTIFICATION("user-configured-notification"),
    UPDATE_RETENTION_POLICY_NOTIFICATION("update-retention-policy-notification"),
    USER_REGISTRATION_REQUEST_NOTIFICATION("user-registration-request-notification"),
    USER_REJECTED_NOTIFICATION("user-rejected-notification"),
    PENDING_USER_REGISTRATION_REQUEST_NOTIFICATION("pending-user-registration-request-notification"),
    BUSINESS_DELETION_NOTIFICATION("business-deletion-notification");

    private final String text;

    TemplateType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
