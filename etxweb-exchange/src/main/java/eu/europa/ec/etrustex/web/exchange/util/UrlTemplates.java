package eu.europa.ec.etrustex.web.exchange.util;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;

/**
 * The Class UrlTemplates is used to define all the <em>PATHS</em> and the <em>PARAMETERS</em> of the REST api.
 *
 * <p>
 *
 * @author fuscoem
 * @see Rels Rels
 * @see LinkUtils LinkUtils
 */
public class UrlTemplates {
    public static final String API_ROOT = "/api/v1";
    private static final String FILE = "/file";
    /* UserController */
    public static final String USERS = API_ROOT + "/users";
    public static final String USER = USERS + "/{ecasId}";
    public static final String USER_DETAILS = USERS + "/details";
    public static final String USER_PREFERENCES = USERS + "/preferences";
    public static final String USER_LIST_ITEMS = USERS + "/list-items";
    /* UserProfileController */
    public static final String USER_PROFILES = API_ROOT + "/user-profiles";
    public static final String USER_PROFILE_DELETE = USER_PROFILES + "/delete";
    public static final String USER_PROFILE_EULOGIN = USER_PROFILES + "/eulogin-info";
    public static final String USER_PROFILE_INFO = USER_PROFILES + "/user-profile-info";
    public static final String USER_PROFILES_SEARCH = USER_PROFILES + "/search";
    public static final String USER_PROFILE_NOTIFICATION_EMAILS = USER_PROFILES + "/notification-emails";
    public static final String USER_PROFILE_SEND_NOTIFICATION_EMAILS = USER_PROFILES + "/send-notification-emails";
    /* GroupController */
    public static final String GROUPS = API_ROOT + "/groups";
    public static final String GROUP = GROUPS + "/{groupId}";
    public static final String GROUP_DELETE = GROUPS + "/group-delete/{groupId}";
    public static final String BUSINESS_DELETE = GROUPS + "/business-delete/{groupId}";
    public static final String IS_VALID_GROUP = GROUPS + "/is-valid";
    public static final String IS_BUSINESS_EMPTY = GROUPS + "/{groupId}/emptiness";
    public static final String IS_GROUP_EMPTY = GROUPS + "/{groupId}/is-empty";
    public static final String BUSINESS_CONFIRM_DELETION = GROUPS + "/{groupId}/confirm";
    public static final String BUSINESS_CANCEL_DELETION = GROUPS + "/{groupId}/cancel";
    /* UserExportController */
    public static final String EXPORT_USERS = GROUP + "/export-users";
    public static final String GROUP_SEARCH = GROUPS + "/search";
    /* RecipientPreferencesController url template */
    public static final String RECIPIENTS_PREFERENCES = API_ROOT + "/recipients-preferences";
    public static final String RECIPIENT_PREFERENCES = RECIPIENTS_PREFERENCES + "/{recipientPreferencesId}";
    /* MessageController */
    public static final String MESSAGES = API_ROOT + "/messages";
    public static final String MESSAGE = MESSAGES + "/{messageId}";
    public static final String MESSAGE_IS_READY_TO_SEND = MESSAGES + "/is-ready-to-send/{messageId}";
    public static final String DRAFT_MESSAGE = MESSAGE + "/draft";
    public static final String SENT_MESSAGE_RECEIPT = MESSAGE + "/receipt";
    public static final String MESSAGE_MONITORING = MESSAGES + "/monitoring";
    public static final String COUNT_UNREAD_SENT_MESSAGES = MESSAGES + "/unread/count";
    public static final String MESSAGE_SUMMARY = MESSAGE + "/message-summary";
    /* MessageSummary */
    public static final String MESSAGE_SUMMARIES = MESSAGES + "/message-summaries";
    public static final String COUNT_UNREAD_MESSAGE_SUMMARIES = MESSAGE_SUMMARIES + "/unread/count";
    public static final String MESSAGE_SUMMARIES_UPDATE = MESSAGE_SUMMARIES + "/update";

    public static final String MESSAGE_SUMMARY_LIST_ITEMS = MESSAGES + "/messageSummaryListItems";
    public static final String MESSAGE_SUMMARY_SEARCH_ITEM = MESSAGES + "/messageSummarySearchItem";
    public static final String MESSAGE_SUMMARY_UPDATE = MESSAGES + "/messageSummaryUpdate";
    /* AttachmentController */
    public static final String ATTACHMENTS = API_ROOT + "/attachments";
    public static final String ATTACHMENT = ATTACHMENTS + "/{attachmentId}";
    /* ExchangeRuleController */
    public static final String EXCHANGE_RULES = API_ROOT + "/rules";
    public static final String EXCHANGE_RULES_BULK = API_ROOT + "/rules/bulk";
    public static final String EXCHANGE_RULES_SEARCH = EXCHANGE_RULES + "/search";
    public static final String EXCHANGE_RULES_BULK_FILE = EXCHANGE_RULES + FILE;
    public static final String EXCHANGE_RULES_PARTICIPANTS_BULK_FILE = EXCHANGE_RULES + "/participants" + FILE;
    public static final String RECIPIENTS_FOR_SENDER = EXCHANGE_RULES + "/recipients-for-sender";
    /* ChannelController */
    public static final String CHANNELS = API_ROOT + "/channels";
    public static final String CHANNELS_BY_GROUP = API_ROOT + "/group-channels";
    public static final String CHANNEL = CHANNELS + "/{channelId}";
    public static final String CHANNELS_SEARCH = CHANNELS + "/search";
    public static final String IS_VALID_CHANNEL = CHANNELS + "/is-valid";
    public static final String EXPORT_CHANNELS = CHANNELS + "/{groupId}/export-channels";
    /* GrantedAuthorityController */
    public static final String GRANTED_AUTHORITIES = API_ROOT + "/granted-authorities";
    public static final String GRANTED_AUTHORITIES_UPDATE_GROUP = GRANTED_AUTHORITIES + "/group";
    public static final String GRANTED_AUTHORITIES_UPDATE_GROUP_BULK = GRANTED_AUTHORITIES_UPDATE_GROUP + "/bulk";
    public static final String GRANTED_AUTHORITIES_CREATE_BULK = GRANTED_AUTHORITIES + "/bulk";
    /* UISettingsController */
    public static final String ENVIRONMENT = "/settings/environment";
    public static final String PUBLIC_KEY = API_ROOT + "/settings/public-key";
    /* AlertController */
    public static final String ALERTS = API_ROOT + "/alerts";
    public static final String TEMPLATES = API_ROOT + "/templates";
    public static final String DEFAULT_TEMPLATES = TEMPLATES + "/default";
    public static final String TEMPLATE = TEMPLATES + "/{businessId}";
    public static final String ALERT = ALERTS + "/{businessId}";
    public static final String ALERT_USER_STATUS = API_ROOT + "/alert-user-status";
    /* GroupConfigurationController */
    public static final String GROUP_CONFIGURATIONS = API_ROOT + "/group-configurations";
    public static final String GROUP_CONFIGURATION = API_ROOT + "/group-configuration/{groupId}";
    /* RedirectController */
    public static final String REDIRECT = "/goto/{redirectId}";
    /* User Guide */
    public static final String USER_GUIDE = API_ROOT + "/user-guide";
    public static final String USER_GUIDE_FILE = USER_GUIDE + FILE;
    public static final String USER_GUIDE_DELETE = USER_GUIDE + "/delete";
    public static final String USER_GUIDES = API_ROOT + "/user-guides/{businessId}";
    public static final String USER_PROFILE_FILE = USER_PROFILES + FILE;
    public static final String GROUP_BULK_FILE = GROUPS + FILE;
    public static final String ATTACHMENT_FILE = ATTACHMENT + FILE;

    public static final String USER_REGISTRATION = API_ROOT + "/user-registration";

    public static final String CERTIFICATE_UPDATE_COMPROMISED_MESSAGES = API_ROOT + "/certificate-update/compromised-messages";
    public static final String CERTIFICATE_UPDATE_IS_VALID_REDIRECT_LINK = API_ROOT + "/certificate-update/is-valid-redirect-link";

    public static final String QUICK_START = API_ROOT + "/quick-start";
    private UrlTemplates() {
        throw new EtxWebException("Utility class");
    }
}
