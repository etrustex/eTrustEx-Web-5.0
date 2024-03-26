package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {
    @SuppressWarnings("java:S1075")
    private static final String PATH = "/myPath/";
    private static final Random RANDOM = new SecureRandom();
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private StorageService storageService;
    private AttachmentService attachmentService;

    @BeforeEach
    public void setUp() {
        this.attachmentService = new AttachmentServiceImpl(messageRepository, messageSummaryRepository, attachmentRepository, storageService, encryptionService);
    }

    @Test
    void should_save_an_attachment() {
        Attachment attachment = mockAttachment();

        given(this.messageRepository.findById(any())).willReturn(Optional.of(attachment.getMessage()));
        given(this.attachmentRepository.save(any())).willReturn(attachment);

        assertThat(attachmentService.save(RANDOM.nextLong(), null)).isEqualTo(attachment);
    }

    @Test
    void should_throw_RuntimeException_when_message_cannot_be_found() {
        given(this.messageRepository.findById(any())).willReturn(Optional.empty());

        Long nextLong = RANDOM.nextLong();
        assertThrows(RuntimeException.class,
                () -> attachmentService.save(nextLong, null));
    }

    @Test
    void should_throw_RuntimeException_when_attachment_cannot_be_saved() {
        Message message = Message.builder()
                .id(RANDOM.nextLong())
                .build();

        given(this.messageRepository.findById(any())).willReturn(Optional.of(message));
        given(this.attachmentRepository.save(any())).willThrow(new EtxWebException("test exception."));

        Long nextLong = RANDOM.nextLong();
        assertThrows(RuntimeException.class,
                () -> attachmentService.save(nextLong, null));
    }

    @Test
    void should_save_an_attachment_object() {
        Attachment attachment = mockAttachment();

        given(this.attachmentRepository.save(any())).willReturn(attachment);

        assertThat(attachmentService.save(any(Attachment.class))).isEqualTo(attachment);
    }

    @Test
    void should_find_by_id() {

        Attachment attachment = mockAttachment();

        given(attachmentRepository.findById(any())).willReturn(Optional.of(attachment));

        assertThat(attachmentService.findById(any())).isEqualTo(attachment);

    }

    @Test
    void should_find_optional_by_id() {
        Attachment attachment = mockAttachment();

        given(attachmentRepository.findById(any())).willReturn(Optional.of(attachment));

        Optional<Attachment> optionalAttachment = attachmentService.findOptionalById(any());
        assertTrue(optionalAttachment.isPresent());
        assertEquals(attachment, optionalAttachment.get());
    }

    @Test
    void should_return_optional_empty() {
        given(attachmentRepository.findById(any())).willReturn(Optional.empty());

        Optional<Attachment> optionalAttachment = attachmentService.findOptionalById(any());
        assertThat(optionalAttachment).isEmpty();

    }

    @Test
    void should_find_by_client_reference_and_message_id() {
        given(attachmentRepository.findByClientReferenceAndMessageId(anyString(), anyLong())).willReturn(Collections.singletonList(mockAttachment()));

        assertThat(attachmentService.findByClientReferenceAndMessageId(anyString(), anyLong()).size()).isOne();

    }

    @Test
    void should_fail_if_append_chunk_called_on_non_existing_attachment() {
        given(attachmentRepository.findById(anyLong())).willReturn(Optional.empty());
        assumeTrue(!AttachmentServiceImpl.class.desiredAssertionStatus());

        assertThrows(AssertionError.class, () -> attachmentService.appendChunkAndChecksum(null, 1L, null));
    }

    @Test
    void should_append_chunk_called_on_existing_attachment() {
        Attachment attachment = mockAttachment();
        attachment.setServerStorePath(PATH);
        byte[] digest = "T".getBytes();

        given(attachmentRepository.findById(anyLong())).willReturn(Optional.of(attachment));
        assumeTrue(!AttachmentServiceImpl.class.desiredAssertionStatus());

        assertEquals(attachmentService.appendChunkAndChecksum(null, 1L, null), digest);
    }

    @Test
    void should_get_resource() {
        Attachment attachment = mockAttachment();
        attachment.setServerStorePath(PATH);
        Path storePath = Paths.get(attachment.getServerStorePath());
        InputStreamResource stubInputStream = new InputStreamResource(IOUtils.toInputStream("some test data", StandardCharsets.UTF_8));

        given(attachmentRepository.findById(anyLong())).willReturn(Optional.of(attachment));
        given(storageService.getFileUploadDir()).willReturn(storePath);
        given(storageService.loadFile(storePath)).willReturn(stubInputStream);

        assertEquals(stubInputStream, attachmentService.getResource(attachment.getId()));
    }

    @Test
    void should_find_message_by_id() {
        Long messageId = 5L;
        Attachment attachment = mockAttachment();
        given(attachmentRepository.findByMessageId(messageId)).willReturn(Collections.singletonList(attachment));
        assertThat(attachmentService.findByMessageId(messageId).get(0).getId()).isEqualTo(attachment.getId());
    }

    @Test
    void should_find_by_message() {
        given(attachmentRepository.findByMessage(any())).willReturn(Collections.singletonList(mockAttachment()));

        assertThat(attachmentService.findByMessage(any()).size()).isOne();
    }

    @Test
    void should_find_by_message_id() {
        given(attachmentRepository.findByMessage(any())).willReturn(Collections.nCopies(3, mockAttachment()));

        assertThat(attachmentService.findByMessage(any()).size()).isEqualTo(3);
    }

    @Test
    void should_delete_attachment() {
        Attachment attachment = mockAttachment();

        given(attachmentRepository.findById(attachment.getId())).willReturn(Optional.of(attachment));

        attachmentService.delete(attachment.getId());

        verify(attachmentRepository).delete(attachment);
    }

    @Test
    void should_delete_all_attachment() {
        List<Attachment> attachments = Lists.list(mockAttachment(), mockAttachment(), mockAttachment());

        attachmentService.deleteAll(attachments);

        verify(attachmentRepository).deleteAll(attachments);
    }

    @Test
    void should_delete_attachment_and_file() {
        Attachment attachment = mockAttachment();
        attachment.setServerStorePath(PATH);
        Path storePath = Paths.get(attachment.getServerStorePath());

        given(attachmentRepository.findById(attachment.getId())).willReturn(Optional.of(attachment));
        given(storageService.getAbsolutePath(attachment.getServerStorePath())).willReturn(storePath);
        attachmentService.delete(attachment.getId());

        verify(attachmentRepository).delete(attachment);
        verify(storageService).delete(storePath);
    }

    @Test
    void should_find_by_client_reference_and_sender_id() {
        Attachment attachment = mockAttachment();
        given(attachmentRepository.findByClientReferenceAndMessageSenderGroupAndServerStorePathIsNotNull(attachment.getClientReference(), attachment.getMessage().getSenderGroup())).willReturn(Collections.nCopies(1, attachment));
        assertThat(attachmentService.findByClientReferenceAndSenderId(attachment.getClientReference(), attachment.getMessage().getSenderGroup())).isNotEmpty();
    }

    private Attachment mockAttachment() {
        Message message = Message.builder()
                .id(RANDOM.nextLong())
                .build();

        return Attachment.builder()
                .id(RANDOM.nextLong())
                .message(message)
                .build();
    }
}
