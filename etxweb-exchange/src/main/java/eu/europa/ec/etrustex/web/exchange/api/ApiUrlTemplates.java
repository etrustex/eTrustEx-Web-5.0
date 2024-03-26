package eu.europa.ec.etrustex.web.exchange.api;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

public class ApiUrlTemplates {
    private ApiUrlTemplates() {
        throw new EtxWebException("Constants class");
    }

    public static final String MESSAGE_API_ROOT = "/message-api/v1";

    public static final String PUBLIC_KEY = MESSAGE_API_ROOT + "/settings/public-key";

    public static final String MESSAGES = MESSAGE_API_ROOT + "/messages";
    public static final String MESSAGE = MESSAGES + "/{messageId}";

    public static final String ATTACHMENTS = MESSAGE_API_ROOT + "/attachments";
    public static final String ATTACHMENT = ATTACHMENTS + "/{attachmentId}";
    public static final String ATTACHMENT_FILE = ATTACHMENT + "/file";

    public static final String UNREAD_MESSAGE_SUMMARIES = MESSAGES + "/message-summaries";

    public static final String MESSAGE_SUMMARY = MESSAGE + "/message-summary";
    public static final String MESSAGE_SUMMARY_STATUS = MESSAGE_SUMMARY + "/status";
    public static final String MESSAGE_SUMMARY_ACK = MESSAGE_SUMMARY + "/ack/{ack}";

    public static final String SENDER_EXCHANGE_RULE = MESSAGE_API_ROOT + "/exchange-rule";
}
