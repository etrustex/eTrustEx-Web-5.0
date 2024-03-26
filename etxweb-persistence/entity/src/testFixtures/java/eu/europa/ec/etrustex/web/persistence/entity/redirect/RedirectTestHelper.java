package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import static eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentsDownloadRedirect.DOWNLOAD_ALL_URL;
import static eu.europa.ec.etrustex.web.persistence.entity.redirect.CertificateUpdateRedirect.CERTIFICATE_UPDATE_URL;
import static eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect.INBOX_URL;
import static eu.europa.ec.etrustex.web.persistence.entity.redirect.SentMessageDetailsRedirect.SENT_MSG_DETAILS_URL;
import static eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect.USER_REGISTRATION_URL;

public class RedirectTestHelper {

    private RedirectTestHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String redirectToInboxMessageDetails(Long groupId, Long messageId) {
        return String.format(INBOX_URL + "details/%s", groupId, messageId);
    }

    public static String redirectToAttachmentDownload(Long groupId, Long messageId, Long attachmentId) {
        return String.format(INBOX_URL + "download/%s/%s", groupId, messageId, attachmentId);
    }

    public static String redirectToAttachmentsDownload(Long groupId, Long messageId) {
        return String.format(DOWNLOAD_ALL_URL, groupId, messageId);
    }

    public static String redirectToSentMessageDetails(Long groupId, Long messageId) {
        return String.format(SENT_MSG_DETAILS_URL, groupId, messageId);

    }

    public static String redirectToUpdateCertificate(Long groupId, String groupIdentifier, Long userId) {
        return String.format(CERTIFICATE_UPDATE_URL, groupId, groupIdentifier, userId);
    }

    public static String redirectToUserRegistration(Long groupId, String groupIdentifier) {
        return String.format(USER_REGISTRATION_URL, groupId, groupIdentifier);
    }
}
