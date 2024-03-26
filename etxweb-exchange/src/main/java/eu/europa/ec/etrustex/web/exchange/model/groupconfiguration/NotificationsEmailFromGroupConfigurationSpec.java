package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationsEmailFromGroupConfigurationSpec  extends GroupConfigurationSpec<String> {
    private String value;
}

