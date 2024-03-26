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
public class SentMessageDetailsRedirect extends MessageRedirect {
    protected static final String SENT_MSG_DETAILS_URL = SENT_URL + "details/%s";

    @Override
    public String getTargetPath() {
        return String.format(SENT_MSG_DETAILS_URL, getGroupId(), getMessageId());
    }
}

