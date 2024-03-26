package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.NAME_LENGTH_ERROR_MSG;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.OPTIONAL_DESCRIPTION_LENGTH_ERROR_MSG;

@Data
@Builder
@AllArgsConstructor
public class BulkExchangeRuleSpec {
    public static final String NULL_CHANNEL_NAME_ERROR_MSG = "Channel name should not be null.";
    public static final String NULL_MEMBER_IDENTIFIER_ERROR_MSG = "Member identifier should not be null.";
    public static final String NULL_EXCHANGE_MODE_ID_ERROR_MSG = "Exchange mode should not be null.";

    @NotBlank(message = NULL_CHANNEL_NAME_ERROR_MSG)
    @Size(max = 255, message = NAME_LENGTH_ERROR_MSG)
    private String channelName;
    @Size(max = 255, message = OPTIONAL_DESCRIPTION_LENGTH_ERROR_MSG)
    private String channelDescription;
    @NotNull(message = NULL_MEMBER_IDENTIFIER_ERROR_MSG)
    private String memberIdentifier;
    @NotNull(message = NULL_EXCHANGE_MODE_ID_ERROR_MSG)
    private ExchangeMode exchangeMode;
    private Long parentGroupId;
}
