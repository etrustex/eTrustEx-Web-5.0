package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@AllArgsConstructor
public class MessageSummaryUserStatusItem implements Serializable {
    private String ecasId;
    private Long messageId;
    private String subject;
    private String name;
    private Date modifiedDate;
}
