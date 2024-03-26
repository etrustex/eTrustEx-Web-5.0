package eu.europa.ec.etrustex.web.service.pdf;

import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.pdf.signature.PdfSignatureService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.text.Alignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;


@Service
@Scope(value = "prototype")
@RequiredArgsConstructor
@Slf4j
public class PDFServiceImpl implements PDFService {

    public static final String DRAFT_HEADER = "This receipt is a DRAFT!";
    public static final String SANS_REGULAR_TTF_PATH = "ttf/NotoSans-Regular.ttf";
    public static final String SANS_BOLD_TTF_PATH = "ttf/NotoSans-Bold.ttf";
    public static final String SANS_CJK_REGULAR_TTF_PATH = "ttf/NotoSansCJKtc-Regular.ttf";
    public static final String SANS_ARABIC_VARIABLE_TTF_PATH = "ttf/NotoSansArabic-VariableFont.ttf";

    private static final int HEADER_FONT_SIZE = 12;
    private static final int STANDARD_FONT_SIZE = 10;

    private final MessageService messageService;
    private final TemplateService templateService;
    private final EncryptionService encryptionService;
    private final PdfSignatureService pdfSignatureService;

    private PDFont standardFont;
    private PDFont boldFont;
    private Document document;

    private MultiLangCharHandler multiLangCharHandler;

    private final SimpleDateFormat iSODateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Pair<String, InputStream> getTransmissionReceipt(Long messageId, String clientPublicKeyPem) {

        Message message = messageService.findById(messageId);
        Group parent = message.getSenderGroup().getParent();

        // we are using PDFBox-Layout on top of PDFBox. see https://github.com/ralfstuckert/pdfbox-layout/wiki for info
        // on the higher level library PDFBox-Layout
        document = new Document(40, 60, 40, 60);

        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            standardFont = PDType0Font.load(document.getPDDocument(), resourceLoader.getResource(SANS_REGULAR_TTF_PATH).getInputStream());
            boldFont = PDType0Font.load(document.getPDDocument(), resourceLoader.getResource(SANS_BOLD_TTF_PATH).getInputStream());
            PDFont cjkFont = PDType0Font.load(document.getPDDocument(), resourceLoader.getResource(SANS_CJK_REGULAR_TTF_PATH).getInputStream());
            PDFont arabicFont = PDType0Font.load(document.getPDDocument(), resourceLoader.getResource(SANS_ARABIC_VARIABLE_TTF_PATH).getInputStream());

            multiLangCharHandler = new MultiLangCharHandler(standardFont, cjkFont, arabicFont);
        } catch (IOException e) {
            throw new EtxWebException("Cannot load Noto fonts!", e);
        }

        addDraftHeaderIfNotSuccessfullySent(message);

        addParagraph("Message Summary", boldFont, Alignment.Center, HEADER_FONT_SIZE);
        br(2);

        addSectionLabel("Subject");
        addMultiLangParagraph(message.getSubject());
        br();


        if (StringUtils.isNotEmpty(message.getText())) {
            // TODO: ETRUSTEX-8449 remove once no unencrypted Message.text remains in the database
            String messageText = (message.getSymmetricKey() != null && StringUtils.isNotBlank(message.getIv()))
                    ? encryptionService.getDecryptedMessageText(message)
                    : message.getText();

            addSectionLabel("Message");
            addMultiLangParagraph(messageText);
            br();
        }

        addClientReferences(message.getMessageSummaries());

        addSectionLabel("Submission completed on");
        addParagraph(message.getSentOn().toString());
        br();

        addSectionLabel("Sender");
        addGroupInfo(message.getSenderGroup());
        br();

        addRecipients(message.getMessageSummaries());
        br();

        addTemplateVariables(templateService.getTemplateVariables(message));
        br();


        addAttachments(message);

        return Pair.of(getReceiptFileName(message), finalizeDocument());
    }


    private String getReceiptFileName(Message message) {
        return DateFormatters.getYearMonthDayFormatter().format(message.getSentOn()) + "-" + "send-receipt.pdf";
    }

    private void addParagraph(String text) {
        addParagraph(text, standardFont, Alignment.Left, STANDARD_FONT_SIZE);
    }

    private void br() {
        br(1);
    }

    private void br(int lines) {
        for (int i = 0; i < lines; i++) {
            addParagraph(" ");
        }
    }

    private void addParagraph(String text, PDFont font, Alignment alignment, int fontSize) {
        if (text == null) {
            return;
        }

        Paragraph paragraph = new Paragraph();
        try {
            paragraph.addText(text, fontSize, font);
        } catch (IOException e) {
            throw new EtxWebException(e);
        }
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    private void addMultiLangParagraph(String text) {
        if (text == null) {
            return;
        }

        Paragraph paragraph = new Paragraph();

        text.chars()
                .mapToObj(i -> (char) i)
                .forEach(ch -> {
                    try {
                        multiLangCharHandler.addChar(ch, paragraph, STANDARD_FONT_SIZE);
                    } catch (IOException e) {
                        throw new EtxWebException(e);
                    }
                });

        paragraph.setAlignment(Alignment.Left);
        document.add(paragraph);
    }


    private void addSectionLabel(String label) {
        addParagraph(label, boldFont, Alignment.Left, STANDARD_FONT_SIZE);
    }

    private void addDraftHeaderIfNotSuccessfullySent(Message message) {
        if (!Status.DELIVERED.equals(message.getStatus())
                && !Status.SENT.equals(message.getStatus())
                && !Status.READ.equals(message.getStatus())
                && !Status.MULTIPLE.equals(message.getStatus())
                && !Status.FAILED.equals(message.getStatus())
        ) {
            addParagraph(DRAFT_HEADER, boldFont, Alignment.Center, HEADER_FONT_SIZE);
        }
    }

    private void addRecipients(Set<MessageSummary> messageSummaries) {

        messageSummaries.forEach(messageSummary -> {
            addSectionLabel("Recipient");

            addGroupInfo(messageSummary.getRecipient());
            addMultiLangParagraph("- Message status: " + messageSummary.getStatus().toString());
            addMultiLangParagraph("- Status date: " + iSODateFormat.format((messageSummary.getStatusModifiedDate())));
        });
    }

    private void addGroupInfo(Group group) {
        addMultiLangParagraph("- Identifier: " + group.getIdentifier());
        addMultiLangParagraph("- Name: " + group.getName());
    }

    private void addClientReferences(Set<MessageSummary> messageSummaries) {
        boolean addBr = false;
        for (MessageSummary messageSummary : messageSummaries) {
            if (StringUtils.isNotEmpty(messageSummary.getClientReference())) {
                addBr = true;
                addSectionLabel("Client reference");
                addParagraph(messageSummary.getClientReference());
            }
        }

        if (addBr) {
            br();
        }
    }

    private InputStream finalizeDocument() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            document.save(byteArrayOutputStream);

            return pdfSignatureService.sign(byteArrayOutputStream);
        } catch (IOException e) {
            throw new EtxWebException(e);
        }
    }


    private void addTemplateVariables(Map<String, Object> templateVariables) {
        if (templateVariables.isEmpty()) {
            return;
        }

        addSectionLabel("Metadata");

        addTemplateVars(templateVariables);

    }

    private void addTemplateVars(Map<String, Object> templateVariables) {
        String[] orderedFields = new String[]{"senderInfo_firstName", "senderInfo_lastName", "senderInfo_email", "messageId", "totalFilesSize"};

        for (String key : orderedFields) {
            if (templateVariables.containsKey(key)) {
                addParagraph(key + ": " + templateVariables.get(key).toString());
            }
        }
    }


    private void addAttachments(Message message) {
        addSectionLabel("Attachments");

        message.getAttachmentSpecs().forEach(baseAttachmentSpec -> addMultiLangParagraph("- " + baseAttachmentSpec.getName() + " (" + baseAttachmentSpec.getByteLength() + " bytes)"));
    }

}
