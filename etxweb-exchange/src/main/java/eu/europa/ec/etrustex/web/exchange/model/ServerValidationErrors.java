package eu.europa.ec.etrustex.web.exchange.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@RequiredArgsConstructor
@Value
public class ServerValidationErrors {
    private final Date timestamp;
    private final int status;
    private final List<String> errors;
}
