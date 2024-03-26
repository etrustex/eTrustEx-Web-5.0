package eu.europa.ec.etrustex.web.common.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateContextConstants {
    TPL_VARIABLES_STATUS("status"),
    TPL_VARIABLES_SUBJECT("subject"),
    TPL_VARIABLES_COMMENT("comment"),
    TPL_VARIABLES_SENDER_NAME("senderName"),
    TPL_VARIABLES_RECIPIENT_USERNAME("recipientUserName"),
    TPL_VARIABLES_RECIPIENT_GROUP_ID("recipientGroupId"),
    TPL_VARIABLES_RECIPIENT_GROUP_NAME("recipientGroupName"),
    TPL_VARIABLES_SENDER_GROUP_ID("senderGroupId"),
    TPL_VARIABLES_SENDER_GROUP_NAME("senderGroupName"),
    TPL_VARIABLES_MESSAGE_ID("messageId"),
    TPL_VARIABLES_MESSAGE_SUBJECT("messageSubject"),
    TPL_VARIABLES_STATUS_CHANGE_DATE("statusChangeDate"),
    TPL_VARIABLES_STATUS_CHANGE_TIME("statusChangeTime"),
    TPL_VARIABLES_NUMBER_OF_FILES("numberOfFiles"),
    TPL_VARIABLES_TOTAL_FILES_SIZE("totalFilesSize"),
    TPL_VARIABLES_MESSAGE_SENT_ON("messageSentOn"),
    TPL_VARIABLES_MESSAGE_EXPIRATION_DATE("messageExpirationDate"),
    TPL_VARIABLES_APPLICATION_URL("applicationURL"),
    TPL_VARIABLES_DELETION_IN_DAYS("deletionInDays"),
    TPL_VARIABLES_USER_NAME("userName"),
    TPL_VARIABLES_GROUP_NAME("groupName"),
    TPL_VARIABLES_FUNCTIONAL_MAILBOX("functionalMailbox"),
    TPL_VARIABLES_PREVIOUS_VALUE("previousValue"),
    TPL_VARIABLES_NEW_VALUE("newValue"),
    TPL_VARIABLES_ENVIRONMENT("environment");




    private final String value;
}
