package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ChannelExportItem {
    private String channelName;
    private String entityName;
    private String entityDisplayName;
    private ExchangeMode entityRole;
}
