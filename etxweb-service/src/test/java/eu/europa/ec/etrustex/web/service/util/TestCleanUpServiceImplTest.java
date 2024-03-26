package eu.europa.ec.etrustex.web.service.util;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestCleanUpServiceImplTest {
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private RedirectRepository redirectRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private MessageUserStatusRepository messageUserStatusRepository;
    private TestCleanUpService testCleanUpService;

    @BeforeEach
    public void init() {
        testCleanUpService = new TestCleanUpServiceImpl(groupRepository, messageRepository, messageService, redirectRepository, messageUserStatusRepository);
    }

    @Test
    void should_clean_up_after_tests() {
        Group entity = mockGroup("entity");
        String businessIdentifier = entity.getParent().getIdentifier();
        String entityIdentifier = entity.getIdentifier();

        Message sentMessage1 = mockMessage(entity);
        Message sentMessage2 = mockMessage(entity);

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(entityIdentifier, businessIdentifier)).willReturn(entity);
        given(messageRepository.findBySenderGroupId(entity.getId())).willReturn(Arrays.asList(sentMessage1, sentMessage2));
        given(redirectRepository.findByGroupId(entity.getId())).willReturn(Collections.singletonList(mockMessageDetailsRedirect(sentMessage1.getId())));

        testCleanUpService.cleanUpAfterTests(businessIdentifier, entityIdentifier);

        verify(redirectRepository, times(1)).deleteAll(any());
    }
}
