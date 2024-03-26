package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AttachmentDownloadRedirect extends MessageRedirect {
    @NotNull
    private Long attachmentId;

    @Override
    public String getTargetPath() {
        return String.format( INBOX_URL + "download/%s/%s" , getGroupId(), getMessageId(), attachmentId);
    }
}

