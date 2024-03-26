package eu.europa.ec.etrustex.web.persistence.repository.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.InboxMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.MessageRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.SentMessageDetailsRedirect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_ENTITY_ID;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessageDetailsRedirect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RedirectRepositoryTest {

    @Autowired
    private RedirectRepository redirectRepository;
    @Autowired
    private MessageRedirectRepository<MessageRedirect> messageRedirectRepository;
    @Autowired
    private InboxMessageDetailsRedirectRepository inboxMessageDetailsRedirectRepository;
    @Autowired
    private SentMessageDetailsRedirectRepository sentMessageDetailsRedirectRepository;

    private Long messageId;
    private SentMessageDetailsRedirect sentMessageDetalsRedirect;
    private InboxMessageDetailsRedirect inboxMessageDetaisRedirect;

    @BeforeEach
    void init() {
        this.messageId = 1234L;
        this.inboxMessageDetaisRedirect = redirectRepository.save(mockMessageDetailsRedirect(messageId));

        this.sentMessageDetalsRedirect = this.redirectRepository.save(SentMessageDetailsRedirect.builder()
                .messageId(this.messageId)
                .groupId(TEST_ENTITY_ID)
                .build());
    }

    @Test
    void should_find_an_inboxMessageDetailsRedirect_by_id() {
        assertTrue(this.redirectRepository.findById(this.inboxMessageDetaisRedirect.getId()).map(InboxMessageDetailsRedirect.class::isInstance)
                .orElse(false));
    }

    @Test
    void should_find_an_inboxMessageDetailsRedirect_by_messageId() {
        assertTrue(this.inboxMessageDetailsRedirectRepository.findFirstByMessageId(messageId).map(InboxMessageDetailsRedirect.class::isInstance)
                .orElse(false));
    }

    @Test
    void should_find_a_sentMessageDetailsRedirect_by_id() {
        assertTrue(this.redirectRepository.findById(this.sentMessageDetalsRedirect.getId()).map(SentMessageDetailsRedirect.class::isInstance)
                .orElse(false));
    }

    @Test
    void should_find_a_sentMessageDetailsRedirect_by_messageId() {
        assertTrue(this.sentMessageDetailsRedirectRepository.findFirstByMessageId(this.messageId).map(SentMessageDetailsRedirect.class::isInstance)
                .orElse(false));
    }

    @Test
    void should_find_redirects_to_sent_and_inbox_message() {
        List<MessageRedirect> redirects = this.messageRedirectRepository.findByMessageId(this.messageId);
        assertTrue(redirects.containsAll(Arrays.asList(this.inboxMessageDetaisRedirect, this.sentMessageDetalsRedirect)));
    }

    @Test
    void should_find_by_group_id() {
        assertEquals(2, redirectRepository.findByGroupId(TEST_ENTITY_ID).size());
    }
}
