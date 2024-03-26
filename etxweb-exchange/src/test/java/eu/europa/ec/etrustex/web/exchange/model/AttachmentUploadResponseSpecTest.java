package eu.europa.ec.etrustex.web.exchange.model;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AttachmentUploadResponseSpecTest {

    @Test
    void should_set_the_checksum() {
        Long attachmentId = 2L;
        byte[] md = "some bytes".getBytes();

        String checksum = Hex.encodeHexString(md).toUpperCase();

        AttachmentUploadResponseSpec attachmentUploadResponseSpec = new AttachmentUploadResponseSpec(md, attachmentId);

        assertEquals(checksum, attachmentUploadResponseSpec.getChecksum());
    }
}
