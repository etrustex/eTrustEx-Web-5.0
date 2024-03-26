package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IntegerGroupConfigurationSpec extends GroupConfigurationSpec<Integer> {
    @NotNull
    private Integer value;
}
