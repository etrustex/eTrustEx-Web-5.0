package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
@Builder
public class GroupSearchItem implements Serializable {
    private Long id;
    private String identifier;
    private String name;
    private String publicKeyFileName;
    private Confidentiality confidentiality;
}
