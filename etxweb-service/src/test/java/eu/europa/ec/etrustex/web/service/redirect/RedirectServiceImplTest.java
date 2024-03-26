package eu.europa.ec.etrustex.web.service.redirect;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.*;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RedirectServiceImplTest {
    private static final Random RANDOM = new SecureRandom();
    private RedirectService redirectService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private RedirectRepository redirectRepository;
    @Mock
    private EtrustexWebProperties etrustexWebProperties;
    private UUID redirectId;
    private Long attachmentId;
    private String clientReference;
    private long messageId;
    private Group testGroup;

    @BeforeEach
    public void setUp() {
        redirectService = new RedirectServiceImpl(redirectRepository, etrustexWebProperties, attachmentService);
        redirectId = UUID.randomUUID();
        attachmentId = RANDOM.nextLong();
        clientReference = "12345678";
        messageId = RANDOM.nextLong();
        testGroup = Group.builder()
                .identifier("testGroupId")
                .name("testGroup").build();
    }

    @Test
    void should_throw_exception_when_redirect_not_found() {
        given(redirectRepository.findById(redirectId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> redirectService.getTargetUrl(redirectId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot get redirect url for Redirect with id: " + redirectId);
    }

    @Test
    void should_redirect_to_message_url() {
        given(redirectRepository.findById(redirectId))
                .willReturn(Optional.of(InboxMessageDetailsRedirect.builder()
                        .messageId(messageId)
                        .groupId(testGroup.getId())
                        .build()));

        assertThat(redirectService.getTargetUrl(redirectId))
                .isEqualTo(etrustexWebProperties.getApplicationUrl() + RedirectTestHelper.redirectToInboxMessageDetails(testGroup.getId(), messageId));
    }

    @Test
    void should_redirect_to_download_url() {
        given(redirectRepository.findById(redirectId))
                .willReturn(Optional.of(AttachmentDownloadRedirect.builder()
                        .messageId(messageId)
                        .attachmentId(attachmentId)
                        .groupId(testGroup.getId())
                        .build()));

        assertThat(redirectService.getTargetUrl(redirectId))
                .isEqualTo(etrustexWebProperties.getApplicationUrl() + RedirectTestHelper.redirectToAttachmentDownload(testGroup.getId(), messageId, attachmentId));
    }

    @Test
    void should_redirect_to_download_all_url() {
        given(redirectRepository.findById(redirectId))
                .willReturn(Optional.of(AttachmentsDownloadRedirect.builder()
                        .messageId(messageId)
                        .groupId(testGroup.getId())
                        .build()));

        assertThat(redirectService.getTargetUrl(redirectId))
                .isEqualTo(etrustexWebProperties.getApplicationUrl() + RedirectTestHelper.redirectToAttachmentsDownload(testGroup.getId(), messageId));
    }

    @Test
    void should_create_the_redirect_to_attachment_download_using_clientReference() {
        given(etrustexWebProperties.getRedirectUrl()).willReturn("http://localhost/goto");
        given(redirectRepository.save(any())).willAnswer(invocation -> {
            Redirect redirect = invocation.getArgument(0);
            redirect.setId(redirectId);
            return redirect;
        });

        given(attachmentService.findByClientReferenceAndMessageId(clientReference, messageId))
                .willReturn(Collections.singletonList(Attachment.builder()
                        .id(attachmentId)
                        .build()));

        assertThat(redirectService.createAttachmentPermalink(testGroup.getId(), messageId, clientReference))
                .isEqualTo(etrustexWebProperties.getRedirectUrl() + "/" + redirectId);
    }

    @Test
    void should_redirect_to_sent_message_details_url() {
        given(redirectRepository.findById(redirectId))
                .willReturn(Optional.of(SentMessageDetailsRedirect.builder()
                        .id(redirectId)
                        .messageId(messageId)
                        .groupId(testGroup.getId())
                        .build()));

        assertThat(redirectService.getTargetUrl(redirectId))
                .isEqualTo(etrustexWebProperties.getApplicationUrl() + RedirectTestHelper.redirectToSentMessageDetails(testGroup.getId(), messageId));
    }
}
