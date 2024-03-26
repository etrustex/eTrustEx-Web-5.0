package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSummaryListItem implements Serializable {
    private Long messageId;
    private String subject;
    private String recipientEntity;
    private Date date;
    private boolean isActive;
}
