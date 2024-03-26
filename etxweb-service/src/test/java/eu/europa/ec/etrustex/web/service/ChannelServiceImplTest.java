package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockBusiness;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockChannel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* Suppress method names false positive. */
class ChannelServiceImplTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ExchangeRuleRepository exchangeRuleRepository;

    private ChannelService channelService;

    @BeforeEach
    public void setUp() {
        this.channelService = new ChannelServiceImpl(this.channelRepository, this.exchangeRuleRepository, this.groupRepository);
    }

    @Test
    void should_get_the_page_of_channels() {
        Channel channel = mockChannel();
        given(channelRepository.findByBusinessIdAndNameContainingIgnoreCase(any(), any(), any())).willReturn(new PageImpl<>(Collections.singletonList(channel)));

        Page<Channel> channelPage = channelService.findByBusinessIdAndName(1L, "", PageRequest.of(0, 1, Sort.by("name")));

        channelPage.stream().findFirst()
                .orElseThrow(() -> new Error("Empty page"));

        Assertions.assertThat(channelPage.getTotalPages()).isOne();
    }

    @Test
    void should_get_a_channel() {
        Channel channel = mockChannel();
        given(channelRepository.findById(any())).willReturn(Optional.of(channel));
        assertEquals(channel, channelService.findById(channel.getId()));
    }

    @Test
    void should_get_the_channels_by_entity() {
        Channel channel = mockChannel();
        given(channelRepository.findByBusinessIdAndEntityId(any(), any())).willReturn(Collections.singletonList(channel));

        List<Channel> channels = channelService.findByBusinessIdAndEntityId(1L, 1L);

        Assertions.assertThat(channels.size()).isOne();
    }

    @Test
    void should_delete_a_channel() {
        Channel channel = mockChannel();
        given(channelRepository.findById(any())).willReturn(Optional.of(channel));
        given(exchangeRuleRepository.findByChannelId(any(), any())).willReturn(Page.empty());
        channelService.delete(channel.getId());
        verify(exchangeRuleRepository, times(0)).delete(any());
        verify(channelRepository, times(1)).delete(channel);
    }

    @Test
    void should_delete_a_channel_and_its_rule() {
        Channel channel = mockChannel();
        ExchangeRule er = ExchangeRule.builder().exchangeMode(ExchangeMode.SENDER).member(Group.builder().identifier("g").build()).build();
        given(channelRepository.findById(any())).willReturn(Optional.of(channel));
        given(exchangeRuleRepository.findByChannelId(any(), any())).willReturn(new PageImpl<>(Collections.singletonList(er))
        );
        channelService.delete(channel.getId());
        verify(exchangeRuleRepository, times(1)).findByChannelId(any(), any());
        verify(exchangeRuleRepository, times(1)).delete(er);
        verify(channelRepository, times(1)).delete(channel);
    }


    @Test
    void should_throw_when_channel_not_found() {
        given(channelRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(EtxWebException.class, () -> channelService.findById(1234L));
    }

    @Test
    void should_search_and_find_channels_by_name() {
        Channel channel = mockChannel();
        given(channelRepository.findByBusinessIdAndNameContainingIgnoreCase(1L, "channel1")).willReturn(Collections.singletonList(channel));
        List<SearchItem> list = channelService.findByBusinessIdAndName(1L,"channel1");
        assertEquals(1, list.size());
        list = channelService.findByBusinessIdAndName(1L,"chXnnel1");
        assertEquals(0, list.size());
    }



    @Test
    void should_create_channel() {
        Group business = mockBusiness();
        Channel channel = mockChannel();
        ChannelSpec channelSpec = ChannelSpec.builder()
                .businessId(business.getId())
                .isActive(true)
                .build();

        given(groupRepository.findByIdAndType(channelSpec.getBusinessId(), GroupType.BUSINESS)).willReturn(Optional.of(business));
        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        assertEquals(channel, channelService.create(channelSpec));
    }

    @Test
    void should_update_channel() {
        Group business = mockBusiness();
        Channel channel = mockChannel();

        ChannelSpec channelSpec = ChannelSpec.builder()
                .businessId(business.getId())
                .isActive(true)
                .name("NewName")
                .description("NewDescription")
                .defaultChannel(false)
                .build();

        Channel channelTarget = mockChannel();
        channelTarget.setName(channelSpec.getName());
        channelTarget.setDescription(channelSpec.getDescription());

        given(channelRepository.findById(any())).willReturn(Optional.of(channel));

        channelService.update(channel.getId(), channelSpec);
        verify(channelRepository, times(1)).save(channelTarget);
    }

    @Test
    void should_throw_if_business_not_found_on_create() {
        ChannelSpec channelSpec = ChannelSpec.builder()
                .businessId(1L)
                .isActive(true)
                .build();

        given(groupRepository.findByIdAndType(anyLong(), any(GroupType.class))).willReturn(Optional.empty());

        assertThrows(EtxWebException.class, () -> channelService.create(channelSpec));
    }
}
