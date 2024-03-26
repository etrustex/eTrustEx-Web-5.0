package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SuppressWarnings({"java:S100"})
class ChannelRepositoryTest {

    private static final String CHANNEL_NAME = "A channel name";
    private static final Random RANDOM = new SecureRandom();

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ExchangeRuleRepository exchangeRuleRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Group business1;
    private Group business2;
    private Group entity1;

    private Channel channel1;


    @BeforeEach
    public void init() {

        business1 = groupRepository.save(mockGroup("Business1", "Business domain 1", null, GroupType.BUSINESS));
        business2 = groupRepository.save(mockGroup("Business2", "Business domain 2", null, GroupType.BUSINESS));
        entity1 = groupRepository.save(mockGroup("Entity1", "Entity domain 1", business1, GroupType.ENTITY));

        channel1 = channelRepository.save(Channel.builder()
                .name(CHANNEL_NAME)
                .business(business1)
                .build());
        Channel channel2 = Channel.builder()
                .name("another channel name")
                .business(business2)
                .build();
        Channel channel3 = Channel.builder()
                .name("another channel on business1")
                .business(business1)
                .build();

        ExchangeRule exchangeRule = ExchangeRule.builder()
                .channel(channel1)
                .member(entity1)
                .exchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build();

        channelRepository.saveAll(Arrays.asList(channel2, channel3));
        exchangeRuleRepository.save(exchangeRule);

    }

    @Test
    void should_return_default_channel(){
        channelRepository.save(Channel.builder()
                .name(CHANNEL_NAME +"3")
                .business(business1)
                .defaultChannel(true)
                .defaultExchangeMode(ExchangeMode.BIDIRECTIONAL)
                .build());

        assertEquals(0, channelRepository.findByBusinessIdAndDefaultChannelIsTrue(business2.getId()).size());
        assertEquals(1, channelRepository.findByBusinessIdAndDefaultChannelIsTrue(business1.getId()).size());
    }

    @Test
    void should_count_channels_by_business() {
        assertEquals(2, channelRepository.countByBusinessId(business1.getId()));
        assertEquals(1, channelRepository.countByBusinessId(business2.getId()));
        assertEquals(0, channelRepository.countByBusinessId(RANDOM.nextLong()));
    }

    @Test
    void should_get_a_page_of_channels() {
        Page<Channel> channelPage = channelRepository.findByBusinessIdAndNameContainingIgnoreCase(business1.getId(), "CHANNEL", PageRequest.of(0, 10, Sort.by("name")));
        assertEquals(2, channelPage.getTotalElements());
        channelPage.forEach(channel -> assertEquals(business1, channel.getBusiness()));
    }

    @Test
    void should_get_channels_by_entity() {
        List<Channel> channels = channelRepository.findByBusinessIdAndEntityId(business1.getId(), entity1.getId());
        assertEquals(1, channels.size());
    }

    @Test
    void should_get_a_channel_by_id() {
        Channel retrieved = channelRepository.findById(channel1.getId())
                .orElseThrow(() -> new Error("Channel not found"));
        assertEquals(channel1, retrieved);
    }

    @Test
    void should_fail_due_to_unique_index() {
        Channel channel = Channel.builder()
                .business(business1)
                .name(CHANNEL_NAME)
                .build();
        channelRepository.save(channel);
        assertThrows(DataIntegrityViolationException.class, () -> channelRepository.findAll());
    }

    @Test
    void should_fail_due_to_not_null_constraint() {
        Channel c = Channel.builder().build();
        assertThrows(DataIntegrityViolationException.class,
                () -> channelRepository.save(c));
    }

    @Test
    void should_exist() {
        assertTrue(channelRepository.existsByBusinessIdAndName(business1.getId(), CHANNEL_NAME));
    }

    @Test
    void should_not_exist() {
        assertFalse(channelRepository.existsByBusinessIdAndName(business2.getId(), CHANNEL_NAME));
    }

}
