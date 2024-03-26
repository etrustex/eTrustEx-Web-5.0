package eu.europa.ec.etrustex.web.exchange.model.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckNoRepetitions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.Patterns.COMMA_SEPARATED_CAPS_AND_NUMBERS;
import static eu.europa.ec.etrustex.web.util.validation.Patterns.EMPTY;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ForbiddenExtensionsGroupConfigurationSpec extends GroupConfigurationSpec<String> {
    @Size(max = 4000, message = FORBIDDEN_EXTENSIONS_MAX_LENGTH_ERROR_MSG)
    @Pattern(regexp = EMPTY + "|" + COMMA_SEPARATED_CAPS_AND_NUMBERS, message = FORBIDDEN_EXTENSIONS_FORMAT_ERROR_MSG)
    @CheckNoRepetitions(message = FORBIDDEN_EXTENSIONS_NO_REPETITIONS)
    private String value;
}
