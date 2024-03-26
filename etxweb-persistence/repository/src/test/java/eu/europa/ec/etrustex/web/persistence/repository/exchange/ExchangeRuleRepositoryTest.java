package eu.europa.ec.etrustex.web.persistence.repository.exchange;


import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExchangeRuleRepositoryTest {
    private static final String INTEGRATION_TESTS_NAMES_PREFIX = "integrationTest_";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ExchangeRuleRepository exchangeRuleRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private List<Group> eiopaCollectors;
    private List<Group> eiopaSendOnlyCollectors;
    private List<Group> eiopaSupervisors;
    private List<Group> eiopaInsurances;

    private List<Group> eiopaCollectorsSupervisorsRecipients;
    private List<Group> eiopaSupervisorsToInsurancesRecipients;

    private Channel collectorsSupervisorsChannel;

    @BeforeEach
    public void init() {

        // EIOPA-style configuration
        Group eiopa = createGroup("eiopa");

        eiopaCollectors = createGroupList(10, "Collector", eiopa);
        eiopaSendOnlyCollectors = createGroupList(3, "SendOnlyCollector", eiopa);
        eiopaSupervisors = createGroupList(5, "supervisor", eiopa);
        eiopaInsurances = createGroupList(8, "insurance", eiopa);

        collectorsSupervisorsChannel = createChannel("CollectorsSupervisors");
        Channel supervisorsToInsurancesChannel = createChannel("Supervisor");

        eiopaCollectors.forEach(group -> createExchangeRule(group, ExchangeMode.BIDIRECTIONAL, collectorsSupervisorsChannel));
        eiopaSendOnlyCollectors.forEach(group -> createExchangeRule(group, ExchangeMode.SENDER, collectorsSupervisorsChannel));

        eiopaSupervisors.forEach(group -> {
            createExchangeRule(group, ExchangeMode.BIDIRECTIONAL, collectorsSupervisorsChannel);
            createExchangeRule(group, ExchangeMode.SENDER, supervisorsToInsurancesChannel);
        });

        eiopaInsurances.forEach(group -> createExchangeRule(group, ExchangeMode.RECIPIENT, supervisorsToInsurancesChannel));


        eiopaCollectorsSupervisorsRecipients = new ArrayList<>();
        eiopaCollectorsSupervisorsRecipients.addAll(eiopaCollectors);
        eiopaCollectorsSupervisorsRecipients.addAll(eiopaSupervisors);

        eiopaSupervisorsToInsurancesRecipients = new ArrayList<>();
        eiopaSupervisorsToInsurancesRecipients.addAll(eiopaInsurances);
        eiopaSupervisorsToInsurancesRecipients.addAll(eiopaSupervisors);

    }

    @Test
    void should_get_the_page_of_exchange_rules_by_channel() {
        Page<ExchangeRule> exchangeRulePage = exchangeRuleRepository.findByChannelId(this.collectorsSupervisorsChannel.getId(), PageRequest.of(0, 10, Sort.by("member.name")));

        assertThat(exchangeRulePage.getTotalElements()).isPositive();
        exchangeRulePage.forEach(exchangeRule -> assertEquals(this.collectorsSupervisorsChannel, exchangeRule.getChannel()));
    }

    @Test
    void should_count_valid_recipients() {
        //EIOPA validRecipients
        Group sender = eiopaCollectors.get(0);
        List<Long> recipients = new ArrayList<>();

        eiopaInsurances.forEach(group -> recipients.add(group.getId()));
        eiopaSupervisors.forEach(group -> recipients.add(group.getId()));

        Long[] recipientIdsArray = recipients.toArray(new Long[0]);

        assertThat(exchangeRuleRepository.countValidRecipientsForSender(sender.getId(), recipientIdsArray)).isEqualTo(eiopaSupervisors.size());
    }

    @Test
    void should_count_0_recipients_for_a_recipient_only() {
        Group sender = eiopaInsurances.get(0);

        Iterable<Group> recipients = groupRepository.findAll();
        List<Long> recipientsList = new ArrayList<>();
        recipients.forEach(group -> recipientsList.add(group.getId()));


        Long[] recipientIdsArray = recipientsList.toArray(new Long[0]);

        assertThat(exchangeRuleRepository.countValidRecipientsForSender(sender.getId(), recipientIdsArray)).isZero();
    }

    @Test
    void should_generate_empty_address_book_for_eiopa_insurances() {
        Group sender = eiopaInsurances.get(0);
        assertThat(exchangeRuleRepository.getValidRecipients(sender.getId())).isEmpty();
    }

    @Test
    void should_add_insurances_and_collectors_and_supervisors_to_supervisors_address_book() {
        Group sender = eiopaSupervisors.get(0);
        List<Group> supervisorAddressBook = exchangeRuleRepository.getValidRecipients(sender.getId());

        assertTrue(supervisorAddressBook.containsAll(eiopaCollectorsSupervisorsRecipients));
        assertTrue(supervisorAddressBook.containsAll(eiopaSupervisorsToInsurancesRecipients));

        assertEquals(eiopaCollectors.size() + eiopaInsurances.size() + eiopaSupervisors.size(), supervisorAddressBook.size());
    }

    @Test
    void should_add_collectors_and_supervisors_to_collectors_and_sendOnlyCollectors_address_books() {
        Group sender = eiopaCollectors.get(0);
        List<Group> collectorAddressBook = exchangeRuleRepository.getValidRecipients(sender.getId());


        Group sendOnlyCollector = eiopaSendOnlyCollectors.get(0);
        List<Group> sendOnlyCollectorAddressBook = exchangeRuleRepository.getValidRecipients(sendOnlyCollector.getId());

        assertTrue(collectorAddressBook.containsAll(eiopaCollectorsSupervisorsRecipients));
        assertTrue(collectorAddressBook.containsAll(sendOnlyCollectorAddressBook));

        assertEquals(eiopaCollectors.size() + eiopaSupervisors.size(), collectorAddressBook.size());
        assertEquals(eiopaCollectors.size() + eiopaSupervisors.size(), sendOnlyCollectorAddressBook.size());
    }

    @Test
    void should_delete_by_member() {
        assertEquals(31, StreamSupport.stream(exchangeRuleRepository.findAll().spliterator(), false).count());
        exchangeRuleRepository.deleteByMember(eiopaInsurances.get(0));
        assertEquals(30, StreamSupport.stream(exchangeRuleRepository.findAll().spliterator(), false).count());
    }

    @SuppressWarnings("SameParameterValue")
    private Group createGroup(String name) {
        return createGroup(name, null);
    }

    private Group createGroup(String name, Group parent) {
        Group group = Group.builder()
                .identifier(INTEGRATION_TESTS_NAMES_PREFIX + name + "_id")
                .name(INTEGRATION_TESTS_NAMES_PREFIX + name)
                .parent(parent)
                .build();
        return groupRepository.save(group);
    }

    private Channel createChannel(String name) {
        Channel channel = Channel.builder()
                .name(INTEGRATION_TESTS_NAMES_PREFIX + name)
                .build();
        return channelRepository.save(channel);
    }

    private void createExchangeRule(Group g, ExchangeMode mode, Channel c) {
        ExchangeRule exchangeRule = ExchangeRule.builder()
                .channel(c)
                .member(g)
                .exchangeMode(mode)
                .build();
        exchangeRuleRepository.save(exchangeRule);
    }

    private List<Group> createGroupList(int size, String baseName, Group parent) {
        List<Group> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(createGroup(baseName + "_" + i, parent));
        }
        return res;
    }
}
