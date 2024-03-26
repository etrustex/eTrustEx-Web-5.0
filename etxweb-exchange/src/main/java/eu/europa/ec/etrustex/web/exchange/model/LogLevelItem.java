package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@AllArgsConstructor
@Builder
public class LogLevelItem implements Serializable {
    private String loggerName;
    private String loggerLevel;
}
