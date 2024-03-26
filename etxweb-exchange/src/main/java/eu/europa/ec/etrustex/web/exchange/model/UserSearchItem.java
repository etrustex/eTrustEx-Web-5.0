package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
public class UserSearchItem implements Serializable {
    private String name;
    private String ecasId;
}
