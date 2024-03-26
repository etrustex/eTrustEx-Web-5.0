package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckExchangeRuleExistsForChannelAndMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CheckExchangeRuleExistsForChannelAndMember
public class ExchangeRuleSpec {
    public static final String NULL_CHANNEL_ID_ERROR_MSG = "Channel ID should not be null.";
    public static final String NULL_MEMBER_ID_ERROR_MSG = "Member ID should not be null.";
    public static final String NULL_EXCHANGE_MODE_ID_ERROR_MSG = "Exchange mode should not be null.";

    @NotNull(message = NULL_CHANNEL_ID_ERROR_MSG)
    private Long channelId;
    @NotNull(message = NULL_MEMBER_ID_ERROR_MSG)
    private Long memberId;
    @NotNull(message = NULL_EXCHANGE_MODE_ID_ERROR_MSG)
    private ExchangeMode exchangeMode;
}
