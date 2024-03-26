package eu.europa.ec.etrustex.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.SentDetailsViewFilter;
import eu.europa.ec.etrustex.web.service.pdf.PDFService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.SENT_MESSAGE_RECEIPT;

@RestController
@RequiredArgsConstructor
public class PDFController {

    private final ApplicationContext applicationContext;

    @GetMapping(value = SENT_MESSAGE_RECEIPT)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(SentDetailsViewFilter.class)
    public ResponseEntity<InputStreamResource> getReceipt(@PathVariable Long messageId,
                                                          @RequestHeader(CLIENT_PUBLIC_KEY_HEADER_NAME) String clientPublicKeyPem) {
        Pair<String, InputStream> receiptPair = applicationContext.getBean(PDFService.class).getTransmissionReceipt(messageId, clientPublicKeyPem);

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(receiptPair.getLeft())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new InputStreamResource(receiptPair.getRight()));
    }
}
