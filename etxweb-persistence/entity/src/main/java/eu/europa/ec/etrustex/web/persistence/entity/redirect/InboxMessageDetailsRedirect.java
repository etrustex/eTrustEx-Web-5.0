package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class InboxMessageDetailsRedirect extends MessageRedirect {
    private static final String INBOX_MSG_DETAILS_URL = INBOX_URL + "details/%s";

    @Override
    public String getTargetPath() {
        return String.format(INBOX_MSG_DETAILS_URL, getGroupId(), getMessageId());
    }
}

