package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class RedirectClassesTest {

    private static final Long GROUP_ID = 1234L;
    private static final Long MESSAGE_ID = 1234L;
    private static final Long USER_ID = 1234L;
    private static final Long ATTACHMENT_ID = 5678L;
    private static final String GROUP_IDENTIFIER = "GROUP_IDENTIFIER";

    @Test
    void check_redirect_to_inbox_message_details() {
        InboxMessageDetailsRedirect inboxMessageDetailsRedirect = InboxMessageDetailsRedirect.builder()
                .groupId(GROUP_ID)
                .messageId(MESSAGE_ID)
                .build();

        assertEquals(RedirectTestHelper.redirectToInboxMessageDetails(GROUP_ID, MESSAGE_ID), inboxMessageDetailsRedirect.getTargetPath());
    }

    @Test
    void check_redirect_to_attachment_download() {
        AttachmentDownloadRedirect attachmentDownloadRedirect = AttachmentDownloadRedirect.builder()
                .groupId(GROUP_ID)
                .messageId(MESSAGE_ID)
                .attachmentId(ATTACHMENT_ID)
                .build();

        assertEquals(RedirectTestHelper.redirectToAttachmentDownload(GROUP_ID, MESSAGE_ID, ATTACHMENT_ID), attachmentDownloadRedirect.getTargetPath());
    }

    @Test
    void check_redirect_to_attachments_download() {
        AttachmentsDownloadRedirect attachmentsDownloadRedirect = AttachmentsDownloadRedirect.builder()
                .groupId(GROUP_ID)
                .messageId(MESSAGE_ID)
                .build();

        assertEquals(RedirectTestHelper.redirectToAttachmentsDownload(GROUP_ID, MESSAGE_ID), attachmentsDownloadRedirect.getTargetPath());
    }

    @Test
    void check_redirect_to_sent_message_details() {
        SentMessageDetailsRedirect sentMessageDetailsRedirect = SentMessageDetailsRedirect.builder()
                .groupId(GROUP_ID)
                .messageId(MESSAGE_ID)
                .build();

        assertEquals(RedirectTestHelper.redirectToSentMessageDetails(GROUP_ID, MESSAGE_ID), sentMessageDetailsRedirect.getTargetPath());
    }

    @Test
    void check_redirect_to_certificate_update() {
        CertificateUpdateRedirect redirect = CertificateUpdateRedirect.builder()
                .groupId(GROUP_ID)
                .userId(USER_ID)
                .groupIdentifier(GROUP_IDENTIFIER)
                .build();

        assertEquals(RedirectTestHelper.redirectToUpdateCertificate(GROUP_ID, GROUP_IDENTIFIER, USER_ID), redirect.getTargetPath());
    }

    @Test
    void check_redirect_to_user_registration() {
        UserRegistrationRedirect redirect = UserRegistrationRedirect.builder()
                .groupId(GROUP_ID)
                .groupIdentifier(GROUP_IDENTIFIER)
                .build();

        assertEquals(RedirectTestHelper.redirectToUserRegistration(GROUP_ID, GROUP_IDENTIFIER), redirect.getTargetPath());
    }
}
