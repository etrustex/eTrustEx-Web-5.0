package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

public class InboxAndSentTestHelper {
    public static Message mockSentMessage(MessageRepository messageRepository) {
        Message m = EntityTestUtils.mockSentMessage(1234L);
        mockSentMessageRepository(messageRepository, m);
        return m;
    }

    public static Message mockSentMessage(MessageRepository messageRepository, Status status) {
        Message m = EntityTestUtils.mockSentMessage(1234L, status);
        mockSentMessageRepository(messageRepository, m);

        return m;
    }

    private static void mockSentMessageRepository(MessageRepository messageRepository, Message m) {
        lenient().when(messageRepository.findByIdAndSenderGroupId(anyLong(), anyLong())).thenReturn(Optional.of(m));
        lenient().when(messageRepository.findBySenderGroupAndStatusNotAndSentOnIsNotNull(any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(m)));
    }

}
