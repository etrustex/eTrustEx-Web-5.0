package eu.europa.ec.etrustex.web.service.pdf.signature;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface PdfSignatureService {
    InputStream sign(ByteArrayOutputStream byteArrayOutputStream);
}
