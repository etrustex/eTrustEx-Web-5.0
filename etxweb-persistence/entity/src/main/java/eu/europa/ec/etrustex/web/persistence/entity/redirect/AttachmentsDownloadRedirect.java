package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class AttachmentsDownloadRedirect extends MessageRedirect {
    protected static final String DOWNLOAD_ALL_URL = INBOX_URL + "download-all/%s";

    @Override
    public String getTargetPath() {
        return String.format(DOWNLOAD_ALL_URL, getGroupId(), getMessageId());
    }
}

