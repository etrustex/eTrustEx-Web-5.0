package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.*;

import java.util.Date;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class HttpNotificationSpec {
    Long messageId;
    String ecasId;
    String entityIdentifier;
    String businessIdentifier;

    Status status;
    Date modifiedDate;
}
