package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.BulkExchangeRuleSpec;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* method names false positive. */
class ExchangeRuleServiceImplTest {
    private ExchangeRuleRepository exchangeRuleRepository;
    private GroupRepository groupRepository;
    private ChannelRepository channelRepository;
    private ChannelService channelService;
    private ExchangeRuleService exchangeRuleService;
    private CSVService csvService;

    @BeforeEach
    public void setUp() {
        this.exchangeRuleRepository = mock(ExchangeRuleRepository.class);
        this.groupRepository = mock(GroupRepository.class);
        this.channelService = mock(ChannelService.class);
        this.csvService = mock(CSVService.class);

        this.exchangeRuleService = new ExchangeRuleServiceImpl(this.exchangeRuleRepository, this.groupRepository, this.channelRepository, this.channelService, this.csvService);
    }

    @Test
    void can_send() {
        Group sender = mockGroup();
        int validRecipients = 3;

        given(this.exchangeRuleRepository.countValidRecipientsForSender(any(), any())).willReturn(validRecipients);

        assertThat(this.exchangeRuleService.canSend(sender.getId(), 1L, 2L, 3L)).isTrue();
    }

    @Test
    void can_not_send() {
        Group sender = mockGroup();
        int validRecipients = 3;

        given(this.exchangeRuleRepository.countValidRecipientsForSender(any(), any())).willReturn(validRecipients);

        assertThat(this.exchangeRuleService.canSend(sender.getId(), 1L, 2L)).isFalse();
    }

    @Test
    void should_get_valid_recipients() {
        Group sender = mockGroup();

        given(this.exchangeRuleRepository.getValidRecipients(sender.getId())).willReturn(Collections.nCopies(3, mockGroup()));

        assertThat(this.exchangeRuleService.getValidRecipients(sender.getId()).size()).isEqualTo(3);
    }

    @Test
    void should_get_a_page_of_exchange_rules() {
        given(this.exchangeRuleRepository.findByChannelIdAndMemberIdentifierOrMemberNameContains(any(), any(), any()))
                .willReturn(new PageImpl<>(Collections.singletonList(ExchangeRule.builder().build())));
        assertThat(this.exchangeRuleService.findByChannelId(1234L, "", PageRequest.of(0, 10)).getTotalElements()).isOne();
    }

    @Test
    void should_search_by_channel() {
        ExchangeRule exchangeRule = mockExchangeRule(ExchangeMode.SENDER);
        given(this.exchangeRuleRepository.findByChannelIdAndMemberIdentifierOrMemberNameContains(any(), any(), any()))
                .willReturn(new PageImpl<>(Collections.singletonList(exchangeRule)));
        assertThat(this.exchangeRuleService.searchByChannelId(1234L, "").size()).isOne();
    }

    @Test
    void should_create_sender_exchange_rules() {
        Group member = mockGroup();
        Channel channel = mockChannel();
        ExchangeRuleSpec exchangeRuleSpec = new ExchangeRuleSpec();

        ExchangeRule exchangeRule = mockExchangeRule(ExchangeMode.SENDER);
        exchangeRuleSpec.setExchangeMode(ExchangeMode.SENDER);
        exchangeRuleSpec.setChannelId(channel.getId());
        exchangeRuleSpec.setMemberId(member.getId());

        given(groupRepository.findByIdIn(any())).willReturn(Collections.singletonList(member));
        given(channelService.findById(any())).willReturn(channel);
        given(exchangeRuleRepository.saveAll(any())).willReturn(Collections.singletonList(exchangeRule));

        assertThat(exchangeRuleService.create(Collections.singletonList(exchangeRuleSpec), false).get(0).getExchangeMode()).isEqualTo(ExchangeMode.SENDER);
    }

    @Test
    void should_not_create_an_existing_exchange_rules() {
        ExchangeRuleSpec exchangeRuleSpec = new ExchangeRuleSpec();
        Group member = mockGroup();
        Channel channel = mockChannel();

        exchangeRuleSpec.setExchangeMode(ExchangeMode.SENDER);
        exchangeRuleSpec.setChannelId(channel.getId());
        exchangeRuleSpec.setMemberId(member.getId());

        given(groupRepository.findByIdIn(any())).willReturn(Collections.singletonList(member));
        given(channelService.findById(any())).willReturn(channel);

        try {
            exchangeRuleService.create(Collections.singletonList(exchangeRuleSpec), false);
        } catch (EtxWebException e) {
            Assertions.assertTrue(e.getMessage().contains("Trying to save an already existing Exchange rule with channel:"));
        }
    }

    @Test
    void should_update_sender_exchange_rules() {
        Group member = mockGroup();
        Channel channel = mockChannel();
        ExchangeRuleSpec exchangeRuleSpec = new ExchangeRuleSpec();

        ExchangeRule exchangeRule = mockExchangeRule(ExchangeMode.SENDER);
        exchangeRuleSpec.setExchangeMode(ExchangeMode.SENDER);

        given(groupRepository.findById(any())).willReturn(Optional.of(member));
        given(channelService.findById(any())).willReturn(channel);
        given(exchangeRuleRepository.save(any())).willReturn(exchangeRule);

        assertThat(exchangeRuleService.update(exchangeRuleSpec).getExchangeMode()).isEqualTo(ExchangeMode.SENDER);
    }

    @Test
    void should_not_update_an_exchange_rules_that_does_not_exist() {
        ExchangeRuleSpec exchangeRuleSpec = new ExchangeRuleSpec();
        Group member = mockGroup();
        Channel channel = mockChannel();

        exchangeRuleSpec.setExchangeMode(ExchangeMode.SENDER);
        exchangeRuleSpec.setChannelId(channel.getId());
        exchangeRuleSpec.setMemberId(member.getId());

        given(groupRepository.findById(any())).willReturn(Optional.of(member));
        given(channelService.findById(any())).willReturn(channel);

        try {
            exchangeRuleService.update(exchangeRuleSpec);
        } catch (EtxWebException e) {
            Assertions.assertTrue(e.getMessage().contains("Trying to update a non existing Exchange rule with channel:"));
        }
    }

    @Test
    void should_delete_an_exchange_rule() {
        Long memberId = 1L;
        Long channelId = 1L;
        exchangeRuleService.delete(memberId, channelId);
        verify(exchangeRuleRepository, times(1)).deleteExchangeRulesByMemberIdAndChannelId(memberId, channelId);

    }

    @Test
    void should_add_participants_bulk() {
        Channel channel = mockChannel();
        ExchangeRule exchangeRule = mockExchangeRule(ExchangeMode.SENDER);
        List<ExchangeRule> exchangeRules = Collections.nCopies(1, exchangeRule);

        given(channelService.findById(any())).willReturn(channel);
        given(csvService.parseCSVFileToExchangeRules(any(), any(), any())).willReturn(exchangeRules);

        exchangeRuleService.bulkAddParticipants("entity1,SENDER".getBytes(), 3L, 23L);

        verify(exchangeRuleRepository, times(1)).saveAll(exchangeRules);
    }

    @Test
    void should_add_exchange_rules_bulk() {
        String channel_bulk = "channel1,channel1 for exchange,entity1, Sender\n" + "channel2,channel2for exchange,entity2, Recipient";
        BulkExchangeRuleSpec bulkExchangeRuleSpec = BulkExchangeRuleSpec.builder().exchangeMode(ExchangeMode.RECIPIENT).parentGroupId(3L).channelName("channel1").memberIdentifier("entity1").build();
        List<BulkExchangeRuleSpec> bulkExchangeRuleSpecs = Collections.nCopies(1, bulkExchangeRuleSpec);
        given(csvService.parseCSVFileToBulkExchangeRuleSpecs(any(), any())).willReturn(bulkExchangeRuleSpecs);

        assertEquals(1,exchangeRuleService.bulkAddExchangeRule(channel_bulk.getBytes(), 3L).size());
    }

}
