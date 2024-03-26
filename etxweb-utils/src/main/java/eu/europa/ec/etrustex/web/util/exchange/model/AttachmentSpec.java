package eu.europa.ec.etrustex.web.util.exchange.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true) // This is to avoid error loading messages that had authTag (only in TEST & DEV)
public class AttachmentSpec implements Serializable {
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private Long id;

    @Deprecated
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String iv;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String name;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String path;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String type;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private long byteLength;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String checkSum;

    private String clientRefId;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String comment;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private boolean confidential;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private long originalByteLength;

    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String originalCheckSum;

    /**
     * @deprecated To be removed with LegacyEncryption for DG Comp
     */
    @Deprecated
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private SymmetricKey symmetricKey;
}
