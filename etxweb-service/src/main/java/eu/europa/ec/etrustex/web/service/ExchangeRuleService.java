package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExchangeRuleService {
    boolean canSend(Long senderId, Long... recipientIds);

    List<Group> getValidRecipients(Long senderId);

    Page<ExchangeRule> findByChannelId(Long channelId, String filterValue, Pageable pageable);

    List<ExchangeRule> searchByChannelId(Long channelId, String filterValue);

    List<ExchangeRule> create(List<ExchangeRuleSpec> exchangeRuleSpec, boolean forced);

    List<String> bulkAddExchangeRule(byte[] fileContent, Long businessIdentifier);
    List<String> bulkAddParticipants(byte[] fileContent, Long businessId, Long channelId);

    ExchangeRule update(ExchangeRuleSpec exchangeRuleSpec);

    void delete(Long memberId, Long channelId);

    ExchangeRule findLastUpdatedExchangeRule(String user);
}
