package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"squid:S1068", "squid:S2160"})
public class ExchangeRuleId implements Serializable {
    private Long channel;
    private Long member;
}
