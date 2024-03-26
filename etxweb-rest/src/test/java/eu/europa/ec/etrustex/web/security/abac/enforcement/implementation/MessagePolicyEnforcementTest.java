package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.etrustex.web.common.UserAction.CREATE;
import static eu.europa.ec.etrustex.web.common.UserAction.UPDATE;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class MessagePolicyEnforcementTest {
    @Mock
    private MessageService messageService;
    @Mock
    private ExchangeRuleService exchangeRuleService;
    @Mock
    private PolicyChecker policyChecker;
    @Mock
    private GroupRepository groupRepository;

    private MessagePolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new MessagePolicyEnforcement(policyChecker, messageService, exchangeRuleService, groupRepository);
    }

    @Test
    void should_get_domain_type() {
        assertEquals(Message.class.getSimpleName(), policyEnforcement.getDomainType());
    }

    @Test
    void should_evaluate_targetId_policy_for_create() {
        SecurityUserDetails userDetails = mockUserDetails();
        Long senderEntityId = 1L;

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, senderEntityId, CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, senderEntityId, CREATE));
    }


    @Test
    void should_evaluate_targetId_policy_for_update() {
        SecurityUserDetails userDetails = mockUserDetails();
        Message message = Message.builder().id(1L).build();

        given(messageService.findById(message.getId())).willReturn(message);

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, message.getId(), UPDATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, message.getId(), UPDATE));
    }

    @Test
    void should_evaluate_object_and_targetId_policy() {
        SecurityUserDetails userDetails = mockUserDetails();
        Group senderGroup = mockGroup();
        Message message = Message.builder().senderGroup(senderGroup).id(1L).build();
        List<Group> recipients = Arrays.asList(mockGroup("recipientId1"), mockGroup("recipientId2"));
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequestSpec(recipients);

        given(messageService.findById(message.getId())).willReturn(message);

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, sendMessageRequestSpec, message.getId(), UserAction.CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, sendMessageRequestSpec, message.getId(), UserAction.CREATE));
    }

    @Test
    void should_throw_if_target_object_wrong_instance() {
        SecurityUserDetails securityUserDetails = mockUserDetails();
        Message message = Message.builder().build();
        assertThrows(EtxWebException.class, () -> policyEnforcement.check(securityUserDetails, message, 1L, UserAction.CREATE));
    }


    private SendMessageRequestSpec mockSendMessageRequestSpec(List<Group> recipients) {
        List<MessageSummarySpec> messageSummarySpecs = new ArrayList<>();

        recipients.forEach(group -> messageSummarySpecs.add(MessageSummarySpec.builder().recipientId(group.getId()).build()));

        return SendMessageRequestSpec.builder()
                .recipients(messageSummarySpecs)
                .build();
    }
}
