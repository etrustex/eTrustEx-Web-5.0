package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.etrustex.web.exchange.model.AttachmentUploadResponseSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.util.crypto.Md;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.collections4.CollectionUtils;
import org.awaitility.Durations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AttachmentControllerTest extends AbstractControllerTest {

    public static final String MESSAGE_ID = "messageId";
    public static final String NUMBER_OF_ATTACHMENTS = "numberOfAttachments";
    private final ResponseFieldsSnippet attachmentResponseFields = relaxedResponseFields(
            fieldWithPath("id").description("The UUID of the generated attachment")
    );
    private final ResponseFieldsSnippet attachmentsResponseFields = relaxedResponseFields(
            fieldWithPath("[].id").description("The UUID of the generated attachment")
    );
    private final PathParametersSnippet attachmentIdPathParametersSnippet = pathParameters(
            parameterWithName("attachmentId").description("The UUID of the attachment")
    );
    public static final String TEST_FILE_STRING = "some test data for my input stream";

    private MessageResult messageResult;
    private Attachment attachment;

    @Autowired
    private MessageUtils messageUtils;


    @AfterEach
    public void cleanUp() {
        messageUtils.cleanUp();
    }

    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
    }


    private void initConfigurations() {
        messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 2, false, false);
    }


    private void happyFlows() throws Exception {
        should_create_an_attachment_without_client_reference();
        should_upload_file();
        should_get_file();
        should_get_an_attachment();
        should_create_an_attachment_with_client_reference_and_status_draft();
    }


    private void securityAccessTests() throws Exception {
        should_forbid_create_an_attachment_with_other_user();
        should_forbid_to_create_an_attachment_if_status_is_not_null();

        should_forbid_get_an_attachment();
        should_forbid_remove_an_attachment();
    }


    void should_create_an_attachment_without_client_reference() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MESSAGE_ID, String.valueOf(messageResult.getMessage().getId()));
        params.add(NUMBER_OF_ATTACHMENTS, "1");

        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.ATTACHMENTS)
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails()))
                                .params(params)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document("should_create_an_attachment",
                                relaxedRequestParameters(
                                        parameterWithName(MESSAGE_ID).description("The ID of the message to which the attachment has to be added"),
                                        parameterWithName(NUMBER_OF_ATTACHMENTS).description("The number of attachments to be created"),
                                        parameterWithName("clientRefs").optional().description("The array of client reference ids")
                                ),
                                attachmentsResponseFields
                        ))
                .andReturn();

        List<Attachment> attachments = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Attachment>>() {
        });
        Attachment newAttachment = attachments.get(0);
        assertNotNull(newAttachment.getId());
        assertNull(newAttachment.getMessage());
        assertTrue(CollectionUtils.isNotEmpty(newAttachment.getLinks()));

        newAttachment.setMessage(messageResult.getMessage());
        attachment = newAttachment;
    }

    void should_create_an_attachment_with_client_reference_and_status_draft() throws Exception {
        messageResult.getMessage().setStatus(Status.DRAFT);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MESSAGE_ID, String.valueOf(messageResult.getMessage().getId()));
        params.add(NUMBER_OF_ATTACHMENTS, "1");

        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.ATTACHMENTS)
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails()))
                                .params(params)
                )
                .andExpect(status().isCreated())
                .andDo(
                        document("should_create_a_number_of_attachment",
                                relaxedRequestParameters(
                                        parameterWithName(MESSAGE_ID).description("The UUID of the message to which the attachment has to be added"),
                                        parameterWithName(NUMBER_OF_ATTACHMENTS).description("The number of attachments to be created")
                                ),
                                attachmentsResponseFields
                        ))
                .andReturn();

        List<Attachment> attachments = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Attachment>>() {
        });
        Attachment newAttachment = attachments.get(0);

        assertNotNull(newAttachment.getId());
        assertNull(newAttachment.getMessage());
        assertTrue(CollectionUtils.isNotEmpty(newAttachment.getLinks()));

    }

    void should_forbid_create_an_attachment_with_other_user() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MESSAGE_ID, String.valueOf(messageResult.getMessage().getId()));
        params.add(NUMBER_OF_ATTACHMENTS, "1");

        mockMvc.perform(
                        post(UrlTemplates.ATTACHMENTS)
                                .with(user(operatorUserDetails))
                                .params(params))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    void should_forbid_to_create_an_attachment_if_status_is_not_null() throws Exception {
        messageResult.getMessage().setStatus(Status.DELIVERED);
        messageRepository.save(messageResult.getMessage());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(MESSAGE_ID, String.valueOf(messageResult.getMessage().getId()));
        params.add(NUMBER_OF_ATTACHMENTS, "1");

        mockMvc.perform(
                        post(UrlTemplates.ATTACHMENTS)
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails()))
                                .params(params))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    void should_get_an_attachment() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.ATTACHMENT, attachment.getId())
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails())))
                .andExpect(status().isOk())
                .andDo(
                        document("should_get_an_attachment",
                                attachmentIdPathParametersSnippet,
                                attachmentResponseFields,
                                RestTestUtils.attachmentHateoasLinks
                        )
                )
                .andReturn();

        Attachment newAttachment = objectMapper.readValue(result.getResponse().getContentAsString(), Attachment.class);
        assertEquals(attachment.getId(), newAttachment.getId());
        assertTrue(CollectionUtils.isNotEmpty(newAttachment.getLinks()));
    }

    @Test
    void should_remove_an_attachment() throws Exception {
        initConfigurations();
        attachment = messageUtils.createMessageAttachment(messageResult.getSenderUserDetails(), messageResult.getMessage());

        mockMvc.perform(
                        delete(UrlTemplates.ATTACHMENT, attachment.getId())
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails())))
                .andExpect(status().isOk())
                .andDo(document("should_remove_attachment",
                        attachmentIdPathParametersSnippet)
                )
                .andReturn();
    }

    void should_upload_file() throws Exception {
        byte[] fakeContent = TEST_FILE_STRING.getBytes();
        MessageDigest md = Md.getSha512MdInstance();
        byte[] fakeDigest = md.digest(fakeContent);

        MvcResult result = mockMvc.perform(
                        put(UrlTemplates.ATTACHMENT_FILE, attachment.getId())
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails()))
                                .param(MESSAGE_ID, String.valueOf(attachment.getMessage().getId()))
                                .param("senderEntityId", String.valueOf(attachment.getMessage().getSenderGroup().getId()))
                                .content(fakeContent)
                                .contentType("application/octet-stream")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("should_upload_file",
                                attachmentIdPathParametersSnippet,
                                relaxedResponseFields(
                                        fieldWithPath("checksum").description("The server-side SHA-512 checksum of the uploaded chunk")
                                ),
                                relaxedRequestParameters(
                                        parameterWithName(MESSAGE_ID).description("The UUID of the message to which the attachment has to be added")
                                ),
                                RestTestUtils.commonAttachmentLinks
                                        .and(linkWithRel(Rels.ATTACHMENT_GET.toString()).optional().description("The attachment upload link"))
                        )
                )
                .andReturn();

        AttachmentUploadResponseSpec attachmentUploadResponseSpec = objectMapper.readValue(result.getResponse().getContentAsString(), AttachmentUploadResponseSpec.class);
        assertEquals(DatatypeConverter.printHexBinary(fakeDigest).toUpperCase(), attachmentUploadResponseSpec.getChecksum());
    }

    void should_get_file() throws Exception {
        Long entityId = messageResult.getSenderEntity().getId();
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.ATTACHMENT_FILE, attachment.getId())
                                .param("entityId", String.valueOf(entityId))
                                .with(SecurityMockMvcRequestPostProcessors.user(messageResult.getSenderUserDetails()))
                )
                .andExpect(status().isOk())
                .andDo(document(
                        "should_get_file",
                        attachmentIdPathParametersSnippet
                ))
                .andReturn();

        await().pollDelay(Durations.FIVE_HUNDRED_MILLISECONDS).until(() -> true);

        String resultsString = result.getResponse().getContentAsString();
        assertEquals(TEST_FILE_STRING, resultsString);
    }

    void should_forbid_get_an_attachment() throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.ATTACHMENT, attachment.getId())
                                .with(user(operatorUserDetails)))
                .andExpect(status().isForbidden());
    }

    void should_forbid_remove_an_attachment() throws Exception {
        mockMvc.perform(
                        delete(UrlTemplates.ATTACHMENT, attachment.getId())
                                .with(user(operatorUserDetails)))
                .andExpect(status().isForbidden());
    }

}
