package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import eu.europa.ec.etrustex.web.service.validation.annotation.UniqueChannelName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@Builder
@UniqueChannelName(message = NAME_UNIQUE_ERROR_MSG, groups = {PostAuthorization.class})
@ToString
public class ChannelSpec {

    @Pattern(regexp = Patterns.TRIMMED, message = NAME_TRIM_ERROR_MSG)
    @NotNull(message = NAME_NOT_NULL_ERROR_MSG)
    @Size(min = 1, max = 255, message = NAME_LENGTH_ERROR_MSG)
    private String name;
    @Size(max = 255, message = OPTIONAL_DESCRIPTION_LENGTH_ERROR_MSG)
    private String description;
    @NotNull(message = BUSINESS_NOT_NULL_ERROR_MSG)
    private Long businessId;
    @NotNull(message = STATUS_NOT_NULL_ERROR_MSG)
    private Boolean isActive;

    private Boolean defaultChannel;
    private ExchangeMode defaultExchangeMode;
}
