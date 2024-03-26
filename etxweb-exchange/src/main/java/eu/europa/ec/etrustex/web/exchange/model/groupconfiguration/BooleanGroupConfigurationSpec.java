package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BooleanGroupConfigurationSpec extends GroupConfigurationSpec<Boolean> {
    @Override
    public Boolean getValue() {
        return this.isActive();
    }
}
