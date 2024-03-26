package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.RETENTION_POLICY_MAX_LENGTH_ERROR_MSG;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RetentionPolicyGroupConfigurationSpec extends GroupConfigurationSpec<Integer> {
    @Max(message = RETENTION_POLICY_MAX_LENGTH_ERROR_MSG, value = 7300)
    private Integer value;
}
