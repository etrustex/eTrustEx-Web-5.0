package eu.europa.ec.etrustex.web.exchange.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import javax.swing.text.html.ListView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class AttachmentUploadResponseSpec implements Serializable {
    @JsonIgnore
    private Long attachmentId;

    private String checksum;
    @JsonView(ListView.class)
    private final List<Link> links = new ArrayList<>();

    public AttachmentUploadResponseSpec(final byte[] md, final Long attachmentId) {
        this.attachmentId = attachmentId;
        this.checksum = Hex.encodeHexString(md).toUpperCase();
    }
}
