package eu.europa.ec.etrustex.web.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesSpec implements Serializable {

    private int paginationSize;
}
