package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.exchange.model.ChannelExportItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRuleId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangeRuleRepository extends CrudRepository<ExchangeRule, ExchangeRuleId> {

    @Query("select count(distinct recipient.member.name) from ExchangeRule sender join ExchangeRule recipient  " +
            "on sender.member.id = :senderId and recipient.member.id in :recipientIds and " +
            "sender.exchangeMode in (eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.SENDER, eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.BIDIRECTIONAL) and " +
            "recipient.exchangeMode in (eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.RECIPIENT, eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.BIDIRECTIONAL) and " +
            "sender.channel = recipient.channel where recipient.member.isActive = true")
    Integer countValidRecipientsForSender(@Param("senderId") Long senderId, @Param("recipientIds") Long... recipientIds);


    @Query("select distinct recipient.member from ExchangeRule sender join ExchangeRule recipient  " +
            "on sender.member.id = :senderId and " +
            "sender.exchangeMode in (eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.SENDER, eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.BIDIRECTIONAL) and " +
            "recipient.exchangeMode  in (eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.RECIPIENT, eu.europa.ec.etrustex.web.common.exchange.ExchangeMode.BIDIRECTIONAL) and " +
            "sender.channel = recipient.channel where recipient.member.isActive = true order by upper(recipient.member.name) asc")
    List<Group> getValidRecipients(@Param("senderId") Long senderId);

    @Query("select distinct new eu.europa.ec.etrustex.web.exchange.model.ChannelExportItem(ch.name, er.member.identifier, er.member.name, er.exchangeMode) " +
            "from ExchangeRule er " +
            "   join Channel ch on ch = er.channel " +
            "where ch.business.id = :businessId order by ch.name")
    List<ChannelExportItem> exportMembersByBusinessId(Long businessId);

    void deleteExchangeRulesByMemberIdAndChannelId(Long memberId, Long channelId);

    @EntityGraph(attributePaths = "member")
    Page<ExchangeRule> findByChannelId(Long channelId, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query(value = "select er " +
            "from ExchangeRule er " +
            "where er.channel.id = :channelId and " +
            "( " +
            "   lower(er.member.identifier) like lower(concat('%', :filterValue,'%')) or" +
            "   lower(er.member.name) like lower(concat('%', :filterValue,'%')) " +
            ")",
            countQuery = "select count(er) " +
                    "from ExchangeRule er " +
                    "where er.channel.id = :channelId and " +
                    "( " +
                    "   lower(er.member.identifier) like lower(concat('%', :filterValue,'%')) or " +
                    "   lower(er.member.name) like lower(concat('%', :filterValue,'%')) " +
                    ")")
    Page<ExchangeRule> findByChannelIdAndMemberIdentifierOrMemberNameContains(Long channelId, String filterValue, Pageable pageable);

    Optional<ExchangeRule> findByChannelIdAndMemberId(Long channelId, Long memberId);
    List<ExchangeRule> findByChannelIdAndMemberIdIn(Long channelId, List<Long> memberId);

    void deleteByMember(Group member);

    @EntityGraph(attributePaths = "member")
    ExchangeRule findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);
}
