package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.springframework.restdocs.hypermedia.LinksSnippet;

import java.util.*;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;


public class RestTestUtils {
    static final LinksSnippet hateoasSelfLink = links(
            linkWithRel(Rels.SELF.toString()).optional().description("The self link")
    );

    public static final String TEXT_BODY = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    public static final Map<String, String> TEMPLATE_VARIABLES = new HashMap<>();

    static LinksSnippet commonAttachmentLinks = links(
            linkWithRel(Rels.ATTACHMENT_UPLOAD.toString()).optional().description("Uploads the attachment file"),
            linkWithRel(Rels.ATTACHMENT_DOWNLOAD.toString()).optional().description("Downloads the attachment file"),
            linkWithRel(Rels.ATTACHMENT_DELETE.toString()).optional().description("Removes the attachment from the message and deletes the uploaded file (if existing)")
    );

    static LinksSnippet attachmentHateoasLinks = commonAttachmentLinks
            .and(linkWithRel(Rels.SELF.toString()).optional().description("The self link"));

    static LinksSnippet messageHateoasLinks = hateoasSelfLink.and(
            linkWithRel(Rels.MESSAGE_UPDATE.toString()).optional().description("Sends the message to the recipients"),
            linkWithRel(Rels.ATTACHMENT_CREATE.toString()).optional().description("Creates a new attachment for the message"),
            linkWithRel(Rels.ATTACHMENT_DOWNLOAD.toString()).optional().description("Downloads attachment for the message"),
            linkWithRel(Rels.MESSAGE_RECEIPT_GET.toString()).optional().description("Download the pdf receipt of the sent message"),
            linkWithRel(Rels.MESSAGE_DRAFT.toString()).optional().description("Create Draft Message"),
            linkWithRel(Rels.MESSAGE_DELETE.toString()).optional().description("Delete Draft Message")
    );

    static LinksSnippet groupHateoasLinks = hateoasSelfLink.and(
            linkWithRel(Rels.GROUP_CREATE.toString()).optional().description("Create a new group"),
            linkWithRel(Rels.GROUPS_GET.toString()).optional().description("Return list of groups"),
            linkWithRel(Rels.GROUP_GET.toString()).optional().description("Return group"),
            linkWithRel(Rels.GROUP_UPDATE.toString()).optional().description("Updates an existing group")
    );

    static LinksSnippet recipientPreferencesHateoasLinks = hateoasSelfLink.and(
            linkWithRel(Rels.RECIPIENT_PREFERENCES_UPDATE.toString()).description("Updates an existing RecipientPreferences")
    );

    static {
        TEMPLATE_VARIABLES.put("document", "<Business defined fields>");
    }

    private RestTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static MessageSummarySpec mockMessageSummarySpec(Long recipientId) {
        return MessageSummarySpec.builder()
                .recipientId(recipientId)
                .integrity(Integrity.MODERATE)
                .confidentiality(Confidentiality.LIMITED_BASIC)
                .build();
    }

    public static SendMessageRequestSpec mockSendMessageRequest(MessageSummarySpec... messageSummarySpecs) {
        return mockSendMessageRequest("Test subject", messageSummarySpecs);
    }

    public static SendMessageRequestSpec mockSendMessageRequestWithEncryptedText(SymmetricKey symmetricKey, String iv, String encryptedText, String encryptedTemplates, MessageSummarySpec... messageSummarySpecs) {
        List<MessageSummarySpec> messageSummarySpecList = new ArrayList<>();
        Collections.addAll(messageSummarySpecList, messageSummarySpecs);

        SendMessageRequestSpec sendMessageRequestSpec;
        sendMessageRequestSpec = SendMessageRequestSpec.builder()
                .symmetricKey(symmetricKey)
                .iv(iv)
                .text(encryptedText)
                .recipients(messageSummarySpecList)
                .subject("Test subject")
                .attachmentsTotalByteLength(1234L)
                .attachmentTotalNumber(3)
                .attachmentSpecs(
                        Arrays.asList(
                                AttachmentSpec.builder()
                                        .name("aName.pdf")
                                        .build(),
                                AttachmentSpec.builder()
                                        .name("another.zip")
                                        .build(),
                                AttachmentSpec.builder()
                                        .name("aThird.doc")
                                        .build()
                        )
                )
                .templateVariables(encryptedTemplates)
                .build();

        sendMessageRequestSpec.setRecipients(messageSummarySpecList);

        return sendMessageRequestSpec;
    }


    public static SendMessageRequestSpec mockSendMessageRequest(String subject, MessageSummarySpec... messageSummarySpecs) {
        List<MessageSummarySpec> messageSummarySpecList = new ArrayList<>();
        Collections.addAll(messageSummarySpecList, messageSummarySpecs);

        SendMessageRequestSpec sendMessageRequestSpec;
        sendMessageRequestSpec = SendMessageRequestSpec.builder()
                .symmetricKey(SymmetricKey.builder()
                        .randomBits("R/uQvLOiv9phaCrZvx98Y6fezsavnirkZAkWpj2fMriR/SMgFa3vaoAbdPKBu997J9fIpUgRY2Vc5AVxPmAH43W0+LwavXQHcBX848PdOINMcvhLEDnwEfTIS10GugtqektzRF4dw60j13rbwi/nyKyOdLbVBcUgrYO96fZqoRQa/ZZ+vYnuEEF2ZlpsOOL6nGE7JwLR7tu9UM+gVrhXgFPxlmEvbFSDcKXlIwRFicm615OkEECNTVPwjFzPg9TwxxXbTL24Z15vi9avXeg6NOqxt1MYZqPE0EJjfchwmyEDDBHJzNdal24XdpK5ST7NQPvcJk0EXp2wZSp0+aZZtg==")
                        .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                        .build())
                .iv("CAYjcAHgja0HGhOpQ1L9Tg==")
                .recipients(messageSummarySpecList)
                .subject(subject)
                .attachmentsTotalByteLength(1234L)
                .attachmentTotalNumber(3)
                .text(TEXT_BODY)
                .attachmentSpecs(
                        Arrays.asList(
                                AttachmentSpec.builder()
                                        .name("aName.pdf")
                                        .build(),
                                AttachmentSpec.builder()
                                        .name("another.zip")
                                        .build(),
                                AttachmentSpec.builder()
                                        .name("aThird.doc")
                                        .build()
                        )
                )
                .templateVariables("{\"document\":\"<Business defined fields>\"}")
                .build();

        sendMessageRequestSpec.setRecipients(messageSummarySpecList);

        return sendMessageRequestSpec;
    }
}
