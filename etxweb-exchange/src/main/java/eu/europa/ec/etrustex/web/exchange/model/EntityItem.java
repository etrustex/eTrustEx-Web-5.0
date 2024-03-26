package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
@Builder
public class EntityItem implements Serializable {
    private String businessIdentifier;
    private String businessName;

    private String entityIdentifier;
    private String entityName;

    private String channelName;
    private ExchangeMode exchangeMode;

    private Boolean active;
    private Confidentiality confidentiality;
    private Boolean hasPublicKey;
}
