package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckEmptyContentActiveConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.SPLASH_SCREEN_CONTENT_EMPTY_ERROR_MSG;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@CheckEmptyContentActiveConfiguration(message = SPLASH_SCREEN_CONTENT_EMPTY_ERROR_MSG)
public class SplashScreenGroupConfigurationSpec extends GroupConfigurationSpec<String> {
    private String value;
}
