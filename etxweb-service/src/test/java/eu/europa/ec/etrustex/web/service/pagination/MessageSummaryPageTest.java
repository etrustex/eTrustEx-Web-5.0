package eu.europa.ec.etrustex.web.service.pagination;

import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.*;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MessageSummaryPageTest {

    @Test
    void should_add_links() {
        int messageSummaryListSize = 5;
        int unreadMessages = 2;
        List<MessageSummary> messageSummaryList = mockListOfMessageSummaries(messageSummaryListSize);

        MessageSummaryPage messageSummaryPage = new MessageSummaryPage(messageSummaryList,
                PageRequest.of(0, 5),
                messageSummaryList.size(),
                unreadMessages);

        messageSummaryPage.forEach(this::addLinksFunction);

        messageSummaryList
                .forEach(messageSummary -> {
                    assertTrue(messageSummary.getLinks().stream()
                            .anyMatch(link -> link.getHref().equals("" + messageSummary.getMessage().getId())));
                    assertTrue(messageSummary.getLinks().stream()
                            .anyMatch(link -> link.getHref().equals("attachment")));
                });
    }

    @Test
    void should_set_unread_messages() {
        int messageSummaryListSize = 5;
        int unreadMessages = 2;
        List<MessageSummary> messageSummaryList = mockListOfMessageSummaries(messageSummaryListSize);

        MessageSummaryPage messageSummaryPage = new MessageSummaryPage(messageSummaryList,
                PageRequest.of(0, 5),
                messageSummaryList.size(),
                unreadMessages);

        assertEquals(unreadMessages, messageSummaryPage.getUnreadMessages());
    }

    private void addLinksFunction(MessageSummary messageSummary) {
        messageSummary.getLinks().addAll(
                Arrays.asList(Link.of("" + messageSummary.getMessage().getId(), Rels.SELF),
                        Link.of("attachment", Rels.ATTACHMENT_GET)));
    }

    private List<MessageSummary> mockListOfMessageSummaries(int size) {
        Group senderBusiness = Group.builder()
                .identifier("senderBusiness")
                .type(BUSINESS)
                .parent(mockGroup(ROOT, "root", "root"))
                .build();
        Group senderGroup = Group.builder()
                .identifier("senderGroup")
                .type(ENTITY)
                .parent(senderBusiness)
                .build();
        Group recipientGroup = Group.builder()
                .identifier("recipientGroup")
                .type(ENTITY)
                .parent(senderBusiness)
                .build();
        User senderUser = User.builder()
                .id(1L)
                .ecasId("ecasId")
                .build();
        UserProfile senderUserProfile = UserProfile
                .builder()
                .user(senderUser)
                .group(senderGroup.getBusinessOrRoot())
                .build();

        List<MessageSummary> messageSummaries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            messageSummaries.add(MessageSummary.builder()
                    .message(Message.builder()
                            .id((long) i)
                            .subject("The subject")
                            .text("The body")
                            .senderUserProfile(senderUserProfile)
                            .sentOn(new Date())
                            .build())
                    .recipient(recipientGroup)
                    .build());
        }

        return messageSummaries;
    }
}
