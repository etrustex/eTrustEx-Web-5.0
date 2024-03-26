package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageUserStatusRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessage;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageUserStatusServiceImplTest {

    @Mock
    private MessageUserStatusRepository messageUserStatusRepository;

    private MessageUserStatusService messageUserStatusService;
    private MessageUserStatus messageUserStatus;
    private User mockUser;
    private final Message message = mockMessage();

    @BeforeEach
    public void setUp() {
        this.messageUserStatusService = new MessageUserStatusServiceImpl(messageUserStatusRepository);

        mockUser = mockUser();
        this.messageUserStatus = MessageUserStatus.builder()
                .user(mockUser)
                .message(message)
                .senderGroupId(message.getSenderGroup().getId())
                .status(Status.READ)
                .build();
    }

    @Test
    void should_create_a_new_msg_user_status() {
        given(this.messageUserStatusRepository.save(any())).willReturn(messageUserStatus);

        assertEquals(Status.READ, messageUserStatusService.createReadMessageUserStatusIfNotExists(message, mockUser, Status.READ).getStatus());
    }


}
