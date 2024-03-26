package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.service.pdf.PDFService;
import eu.europa.ec.etrustex.web.service.pdf.PDFServiceImpl;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockAttachment;
import static eu.europa.ec.etrustex.web.service.pdf.PDFServiceImpl.DRAFT_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SuppressWarnings({"java:S100"}) /* Suppress method names false positive */
@ExtendWith(MockitoExtension.class)
class PDFServiceTest {
    @TempDir
    File temporaryFolder;

    @Mock
    private MessageService messageService;
    @Mock
    private TemplateService templateService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private EncryptionService encryptionService;
    private PDFService pdfService;

    @BeforeEach
    void init() {
        pdfService = new PDFServiceImpl(messageService, templateService, encryptionService,
                byteArrayOutputStream -> new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

    }

    @Test
    void should_create_the_transmission_receipt() throws IOException {
        Message message = mockMessage();
        message.setStatus(Status.SENT);
        Date now = new Date();

        Pair<String, File> nameAndFile = runCreateReceiptTest(message, now);

        assertTrue(Files.size(nameAndFile.getRight().toPath()) > 0);
        assertEquals(DateFormatters.getYearMonthDayFormatter().format(now) + "-" + "send-receipt.pdf", nameAndFile.getLeft());
    }

    @Test
    void should_add_edma_documents() throws IOException {
        Message message = mockMessage();
        message.setStatus(Status.SENT);
        Date now = new Date();

        List<AttachmentSpec> attachmentSpecs = Arrays.asList(AttachmentSpec.builder()
                        .name("file.getName()")
                        .path("file.getPath()")
                        .byteLength(1234)
                        .checkSum("getFileChecksum(file).toUpperCase()")
                        .build(),
                AttachmentSpec.builder()
                        .name("file.getName()")
                        .path("file.getPath()")
                        .byteLength(1234)
                        .checkSum("getFileChecksum(file).toUpperCase()")
                        .build());

        message.setAttachmentSpecs(attachmentSpecs);

        given(attachmentService.findOptionalById(attachmentSpecs.get(0).getId())).willReturn(Optional.of(mockAttachment(message)));
        given(attachmentService.findOptionalById(attachmentSpecs.get(1).getId())).willReturn(Optional.of(mockAttachment(message)));

        Pair<String, File> nameAndFile = runCreateReceiptTest(message, now);

        assertTrue(Files.size(nameAndFile.getRight().toPath()) > 0);
        assertEquals(DateFormatters.getYearMonthDayFormatter().format(now) + "-" + "send-receipt.pdf", nameAndFile.getLeft());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void should_set_the_receipt_as_draft() throws IOException {
        Message message = mockMessage();
        message.setStatus(Status.DRAFT);
        Date now = new Date();

        Pair<String, File> nameAndFile = runCreateReceiptTest(message, now);

        PDDocument document = PDDocument.load(nameAndFile.getRight());
        String textContent = new PDFTextStripper().getText(document);

        assertTrue(textContent.startsWith(DRAFT_HEADER));
    }

    private Pair<String, File> runCreateReceiptTest(Message message, Date sentOn) throws IOException {
        message.setSentOn(sentOn);
        message.setText("Лорем ипсум долор сит амет, те вис еним апериам. Те сит мутат глориатур садипсцинг, перфецто елецтрам неглегентур еум не, луцилиус демоцритум цомпрехенсам еу еум. Нибх риденс видерер ут усу, ан цум алии бруте легимус, ех дуо тале нонумес. Нец ипсум децоре евертитур ет. Не вим ипсум сонет утамур, но симул моллис партиендо яуи.\n" +
                "Нобис легере аццусата хас не. Алиа дефинитионем меа ид. Еи легимус постулант яуи, яуод поссит луцилиус еум цу. Сит те пауло садипсцинг. Еи меис цопиосае делицатиссими нам, нихил адиписцинг интеллегебат те вим. Еа молестиае волуптариа еффициенди вих. Еу сит симул тимеам делецтус.\n" +
                "Вих популо садипсцинг ин. Цу меа хинц ипсум зрил. Фацилис медиоцритатем ут цум, меи вениам лаореет ан. Хис веро сцрипсерит ут. Саепе ментитум сцрипсерит иус ид, но магна цонцлудатуряуе цум, диам бонорум неглегентур ет цум. Модо оптион нец ут, иусто оффициис еррорибус ут дуо.\n" +
                "Доцтус аетерно цонституам ат цум, сит но реяуе волутпат губергрен. Модо чоро репрехендунт ид сед, вел цу еирмод адиписци ехпетендис. Цу мел темпор тритани аццусам, при видит цонцлудатуряуе еи. Ан цонсеяуат аргументум усу, солум дицунт симилияуе про еу. Ест яуас фацилисис ин, меа мутат долоре елигенди ин.\n" +
                "Татион луптатум сед но, еос волумус десерунт адверсариум те. Вис суас адверсариум тхеопхрастус ан, номинави реферрентур ех еум. Ан пер мовет оффендит евертитур. Цибо антиопам ирацундиа нец ут, аудире инцидеринт цум ех.\n" +
                "Вел не мутат утрояуе. Аццусамус еффициантур еос не, еум мунди лаудем цоммодо не, ан вис цонгуе неморе алияуип. Сед садипсцинг диссентиунт делицатиссими но, иус ирацундиа адверсариум ех, фацилиси аццусамус форенсибус вих еу. Яуис инструцтиор пер ут.\n" +
                "Ин дуо еним стет ессе, лаудем феугиат мнесарчум ест ут. Вим миним воцент перципитур ан, меи омнис воцент еа, нулла интерессет при те. Хис но модус тимеам, диссентиас сцрипсерит еу меа, ерат симул вертерем еос ат. Ин дуо меис цонвенире, пер ет лорем вивендум, иус цоммуне яуалисяуе еи. Нисл епицури малуиссет сеа ад, вис хабео лаборес ет.\n" +
                "Епицуреи хендрерит пер ут. Сед синт урбанитас десеруиссе еи. Сеа алиа чоро адиписци не, сцрипсерит репудиандае репрехендунт но яуи, еуисмод хабемус перципит ат иус. Идяуе нонумы диссентиас ид дуо, не синт тибияуе пхилосопхиа иус.\n" +
                "Ет при иусто рецусабо. Меа сплендиде адолесценс еи, вис ессе ноструд ат. Солута яуидам ет сед, пер ет велит вереар губергрен. Ат меи вери утинам аццусам, про нихил либрис ад.\n" +
                "Еа еам цоррумпит дефинитионем, при ут яуод еиус сале. Яуо ат дицам лаудем, еа ест апериам пробатус партиендо, ех фуиссет перицулис вим. Аеяуе темпор цум цу. Диам виси дуо те, ан харум симул граеци вис, ан нам долорум тибияуе ехплицари. Хас хабео реферрентур не, меи вереар индоцтум трацтатос ан. Те нобис делицата ест, ех перфецто аццусамус." +
                "Λορεμ ιπσθμ δολορ σιτ αμετ, ινσολενσ ρεπθδιανδαε σιτ νο, νιβη ατqθι ιμπετθσ cθ πρι. Αν δθο qθαεστιο τραcτατοσ cορρθμπιτ. Δθο τε δεcορε μελιορε vθλπθτατε. Εαμ νε αλιι νθλλα ιθδιcαβιτ, αδ ηισ ζριλ ηομερο. Ενιμ vενιαμ φαcετε ετ σεα.\n" +
                "\n" +
                "Ηισ ετ αδηθc σcριπτα, vισ δελεcτθσ ποσιδονιθμ νο, δθο ατ νθμqθαμ ιντερεσσετ. Vιρτθτε μεδιοcριτατεμ εξ εοσ, εα ρεqθε διcερετ εαμ. Τεμπορ δοcτθσ δελενιτι ηισ νο, αδ πρι ελιτρ vιvενδθμ. Εθμ αλιι ειρμοδ εξ, δισcερε οcθρρερετ αδ vισ. Αλιι ιπσθμ qθο αν, ατ βρθτε ερθδιτι qθο.\n" +
                "Vιδισσε λαβιτθρ δενιqθε ει ηισ, φαλλι ινcιδεριντ εξ δθο. Εα πριμα qθαλισqθε qθο. Μεισ δισcερε cονσετετθρ μεα ατ. Προβατθσ νομινατι δισπθτανδο ιν qθι.\n" +
                "Εοσ νε ηομερο αccθσαμ λαβοραμθσ, μεισ μολεστιαε δεφινιτιονεμ νε vελ, θσθ αν λεγερε ασσθεvεριτ. Ατ νοστρο σπλενδιδε θσθ, ναμ ταcιματεσ περιcθλισ πατριοqθε εξ. Εα απεριρι vολθπτατθμ qθαερενδθμ εοσ, μοδθσ δεφινιεβασ δελιcατισσιμι νο περ. Εθ εοσ vιvενδθμ ρεcθσαβο, ναμ ιδ νοστρθδ vθλπθτατε εφφιcιενδι. Αδ ιγνοτα οπορτερε εαμ, cθμ δολορθμ ιντελλεγατ εα. Σεα ιν διcθντ μαλορθμ μενανδρι, δθισ εθριπιδισ ναμ αδ. Εθμ ατ ελεcτραμ περιcθλισ, σιτ αλια νομιναvι ρεπθδιαρε cθ.\n" +
                "Εξ αλιι γραεcισ παρτιενδο vισ, ετ νεc αccθμσαν σεντεντιαε νεγλεγεντθρ. Δθο οφφιcιισ σεντεντιαε cονσετετθρ ιδ. Ειθσ εξερcι αππετερε σιτ νε, γραεcι διcερετ ορνατθσ εθμ αν. Οδιο vολθπτατθμ εθμ ιν, δολορ qθανδο αδ σεα, εθ μει δθισ λαορεετ cονcλθσιονεμqθε.\n" +
                "Qθο τεμπορ vολθπτθα ει. Ενιμ vιδιτ εξ εθμ. Νο ενιμ απεριαμ vιμ. Cονσθλ cοπιοσαε λθπτατθμ θτ vισ, φθγιτ cομμοδο τηεοπηραστθσ νε ναμ.\n" +
                "Σολετ τολλιτ δισπθτατιονι cθ σιτ, ει vιμ ταcιματεσ ρεφορμιδανσ. Νε vιμ διcερετ ερθδιτι. Qθο νε ηαρθμ σαπερετ σεντεντιαε, ει ηισ νεμορε vοcιβθσ φαcιλισι. Σολθμ ομνεσqθε ατ θσθ, τε vολθμθσ νομιναvι vελ, οδιο οπορτερε ηονεστατισ εοσ εα. Εξερcι προπριαε ελοqθεντιαμ vισ νο, ατ μεα ποσσιτ τηεοπηραστθσ. Θτ vιμ αφφερτ qθιδαμ ρεπρεηενδθντ, vιμ νο διαμ εθισμοδ.\n" +
                "Μοδο δετραcτο σcριπτορεμ ιν προ, μεα μθcιθσ ελαβοραρετ ατ, νο δελενιτ νονθμεσ σθαvιτατε cθμ. Προ νο ινvιδθντ cονστιτθτο, ηισ στετ εσσεντ ομνεσqθε ει, σcριπσεριτ ρεφερρεντθρ νο ηασ. Ηασ εα οδιο πθρτο ποσσε. Αδ qθι μινιμθμ περφεcτο ρεπθδιανδαε, πρι ρατιονιβθσ cονcλθδατθρqθε vιτθπερατοριβθσ τε. Ιδ θσθ μοvετ αδvερσαριθμ δελιcατισσιμι.\n" +
                "Ετ ηαβεο ρεπριμιqθε ιθσ, ει εθμ ομνιθμ πατριοqθε. Ατ cασε μθνερε σαλθτατθσ εοσ, ιν νονθμυ vεριτθσ εοσ. Αδ qθι ενιμ σολεατ, σενσεριτ σαπιεντεμ δετερρθισσετ εθ προ. Νεc περcιπιτ νεγλεγεντθρ θτ, ηαβεμθσ περφεcτο qθαερενδθμ εξ qθι. Εξ ετιαμ ιντεγρε δισσεντιθντ θσθ, νο cθμ μοδο σθασ φεθγιατ.\n" +
                "Τε νεc διcτασ μαλθισσετ, vελ θτ qθαεqθε αccθσατα τραcτατοσ. Σεα περcιπιτ αβηορρεαντ ατ. Ιν μαλορθμ ερθδιτι ηισ. Vελ σολετ πλαcερατ ετ, δελενιτ γθβεργρεν ετ σιτ, αν vιξ σθασ φεθγιατ. Εα πορρο νοστρθδ πετεντιθμ.");

        given(messageService.findById(anyLong())).willReturn(message);
        given(templateService.getTemplateVariables(message)).willReturn(mockTemplateVariables(message));

        Pair<String, InputStream> receiptPair = pdfService.getTransmissionReceipt(message.getId(), "pubKey");

        File receipt = new File(temporaryFolder, "receipt.pdf");
        Files.copy(receiptPair.getRight(), receipt.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return Pair.of(receiptPair.getLeft(), receipt);
    }

    private Message mockMessage() {
        return EntityTestUtils.mockMessage();
    }

    private Map<String, Object> mockTemplateVariables(Message message) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("Subject", message.getSubject());
        templateVariables.put("A key", "a value");
        templateVariables.put("Key to a long string", "A long string with newlines \n and bound to be exceeding " +
                "a single line, to check how the pdf behaves in this case.");
        templateVariables.put("A list of emails", "email@foo.bar, email@foo.bar,email@foo.bar,email@foo.bar,email@foo.bar");

        return templateVariables;
    }
}
