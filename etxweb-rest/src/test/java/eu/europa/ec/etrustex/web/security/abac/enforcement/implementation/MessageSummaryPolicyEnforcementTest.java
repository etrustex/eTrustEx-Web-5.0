package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static eu.europa.ec.etrustex.web.common.UserAction.RETRIEVE;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessageSummary;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class MessageSummaryPolicyEnforcementTest {
    private static final String RECIPIENT_ENTITY_ID_KEY = "recipientEntityId";
    private static final String MESSAGE_SUMMARY_KEY = "messageSummary";

    private static final UserAction USER_ACTION = RETRIEVE;
    private static final SecurityUserDetails USER_DETAILS = mockUserDetails();
    public static final Group RECIPIENT_ENTITY = mockGroup();
    public static final EntitySpec ENTITY_SPEC = EntitySpec.builder()
            .entityIdentifier(RECIPIENT_ENTITY.getIdentifier())
            .businessIdentifier(RECIPIENT_ENTITY.getBusinessIdentifier())
            .build();
    public static final Message MESSAGE = Message.builder().id(1L).build();
    public static final MessageSummary MESSAGE_SUMMARY = mockMessageSummary(MESSAGE, RECIPIENT_ENTITY);

    @Mock
    private GroupService groupService;
    @Mock
    private PolicyChecker policyChecker;
    @Mock
    private MessageSummaryService messageSummaryService;

    @Captor
    private ArgumentCaptor<PolicyRuleContext> policyRuleContextArgumentCaptor;

    private MessageSummaryPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new MessageSummaryPolicyEnforcement(policyChecker, groupService, messageSummaryService);
    }

    @Test
    void should_get_domain_type() {
        assertEquals(MessageSummary.class.getSimpleName(), policyEnforcement.getDomainType());
    }

    @Test
    void should_check_for_EntitySpec() {
        given(groupService.findByIdentifierAndParentIdentifier(ENTITY_SPEC.getEntityIdentifier(), ENTITY_SPEC.getBusinessIdentifier())).willReturn(RECIPIENT_ENTITY);
        given(policyChecker.check(policyRuleContextArgumentCaptor.capture())).willReturn(true);

        assertTrue(policyEnforcement.check(USER_DETAILS, ENTITY_SPEC, USER_ACTION));

        PolicyRuleContext policyRuleContext = policyRuleContextArgumentCaptor.getValue();
        Map<String, Object> environment = policyRuleContext.getEnvironment();

        assertEquals(USER_DETAILS, policyRuleContext.getPrincipal());
        assertEquals(USER_ACTION.toString(), policyRuleContext.getAction());
        assertEquals(MessageSummary.class.getSimpleName(), policyRuleContext.getTarget());
        assertEquals(RECIPIENT_ENTITY.getId(), environment.get(RECIPIENT_ENTITY_ID_KEY));
        assertNull(environment.get(MESSAGE_SUMMARY_KEY));


        given(groupService.findByIdentifierAndParentIdentifier(ENTITY_SPEC.getEntityIdentifier(), ENTITY_SPEC.getBusinessIdentifier())).willReturn(null);
        assertFalse(policyEnforcement.check(USER_DETAILS, ENTITY_SPEC, USER_ACTION));


        assertThrows(EtxWebException.class, () -> policyEnforcement.check(USER_DETAILS, MessageSummary.builder().build(), USER_ACTION));
    }

    @Test
    void should_check_for_EntitySpec_and_messageId_args() {
        given(groupService.findByIdentifierAndParentIdentifier(ENTITY_SPEC.getEntityIdentifier(), ENTITY_SPEC.getBusinessIdentifier())).willReturn(RECIPIENT_ENTITY);
        given(messageSummaryService.findByMessageIdAndRecipientId(MESSAGE.getId(), RECIPIENT_ENTITY.getId())).willReturn(MESSAGE_SUMMARY);
        given(policyChecker.check(policyRuleContextArgumentCaptor.capture())).willReturn(true);

        assertTrue(policyEnforcement.check(USER_DETAILS, ENTITY_SPEC, MESSAGE.getId(), USER_ACTION));

        PolicyRuleContext policyRuleContext = policyRuleContextArgumentCaptor.getValue();
        Map<String, Object> environment = policyRuleContext.getEnvironment();

        assertEquals(USER_DETAILS, policyRuleContext.getPrincipal());
        assertEquals(USER_ACTION.toString(), policyRuleContext.getAction());
        assertEquals(MessageSummary.class.getSimpleName(), policyRuleContext.getTarget());
        assertEquals(RECIPIENT_ENTITY.getId(), environment.get(RECIPIENT_ENTITY_ID_KEY));
        assertEquals(MESSAGE_SUMMARY, environment.get(MESSAGE_SUMMARY_KEY));


        given(groupService.findByIdentifierAndParentIdentifier(ENTITY_SPEC.getEntityIdentifier(), ENTITY_SPEC.getBusinessIdentifier())).willReturn(null);
        assertFalse(policyEnforcement.check(USER_DETAILS, ENTITY_SPEC, MESSAGE.getId(), USER_ACTION));


        assertThrows(EtxWebException.class, () -> policyEnforcement.check(USER_DETAILS, MESSAGE_SUMMARY, MESSAGE.getId(), USER_ACTION));
    }

    @Test
    void should_check_for_recipientId_and_messageId_args() {
        given(groupService.findById(RECIPIENT_ENTITY.getId())).willReturn(RECIPIENT_ENTITY);
        given(messageSummaryService.findByMessageIdAndRecipientId(MESSAGE.getId(), RECIPIENT_ENTITY.getId())).willReturn(MESSAGE_SUMMARY);
        given(policyChecker.check(policyRuleContextArgumentCaptor.capture())).willReturn(true);

        assertTrue(policyEnforcement.check(USER_DETAILS, RECIPIENT_ENTITY.getId(), MESSAGE.getId(), USER_ACTION));

        PolicyRuleContext policyRuleContext = policyRuleContextArgumentCaptor.getValue();
        Map<String, Object> environment = policyRuleContext.getEnvironment();

        assertEquals(USER_DETAILS, policyRuleContext.getPrincipal());
        assertEquals(USER_ACTION.toString(), policyRuleContext.getAction());
        assertEquals(MessageSummary.class.getSimpleName(), policyRuleContext.getTarget());
        assertEquals(RECIPIENT_ENTITY.getId(), environment.get(RECIPIENT_ENTITY_ID_KEY));
        assertEquals(MESSAGE_SUMMARY, environment.get(MESSAGE_SUMMARY_KEY));


        given(groupService.findById(RECIPIENT_ENTITY.getId())).willReturn(null);
        assertFalse(policyEnforcement.check(USER_DETAILS, RECIPIENT_ENTITY.getId(), MESSAGE.getId(), USER_ACTION));
    }

    @Test
    void should_check_for_recipientId() {
        given(groupService.findById(RECIPIENT_ENTITY.getId())).willReturn(RECIPIENT_ENTITY);
        given(policyChecker.check(policyRuleContextArgumentCaptor.capture())).willReturn(true);

        assertTrue(policyEnforcement.check(USER_DETAILS, RECIPIENT_ENTITY.getId(), USER_ACTION));

        PolicyRuleContext policyRuleContext = policyRuleContextArgumentCaptor.getValue();
        Map<String, Object> environment = policyRuleContext.getEnvironment();

        assertEquals(USER_DETAILS, policyRuleContext.getPrincipal());
        assertEquals(USER_ACTION.toString(), policyRuleContext.getAction());
        assertEquals(MessageSummary.class.getSimpleName(), policyRuleContext.getTarget());
        assertEquals(RECIPIENT_ENTITY.getId(), environment.get(RECIPIENT_ENTITY_ID_KEY));
        assertNull(environment.get(MESSAGE_SUMMARY_KEY));


        given(groupService.findById(RECIPIENT_ENTITY.getId())).willReturn(null);
        assertFalse(policyEnforcement.check(USER_DETAILS, RECIPIENT_ENTITY.getId(), USER_ACTION));
    }
}
