package eu.europa.ec.etrustex.web.service.pdf;

import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;

public interface PDFService {
    Pair<String, InputStream> getTransmissionReceipt(Long messageId, String clientPublicKeyPem);
}
