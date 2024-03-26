package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityGrantedAuthority;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import eu.europa.ec.etrustex.web.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttachmentPolicyEnforcementTest {
    @Mock
    private MessageService messageService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private PolicyChecker policyChecker;

    @Captor
    private ArgumentCaptor<PolicyRuleContext> policyRuleContextArgumentCaptor;

    private AttachmentPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new AttachmentPolicyEnforcement(policyChecker, attachmentService,messageService);
    }

    @Test
    void should_get_domain_type() {
        assertEquals(Attachment.class.getSimpleName(), policyEnforcement.getDomainType());
    }

    @Test
    void should_evaluate_targetId_policy_for_CREATE() {
        SecurityUserDetails userDetails = mockUserDetails();
        Message message = Message.builder().id(1L).build();

        given(messageService.findById(message.getId())).willReturn(message);


        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, message.getId(), UserAction.CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, message.getId(), UserAction.CREATE));
    }

    @Test
    void should_evaluate_targetId_policy_for_UPDATE() {
        SecurityUserDetails userDetails = mockUserDetails();
        Message message = Message.builder().id(1L).build();
        Attachment attachment = Attachment.builder().id(1L).message(message).build();

        given(attachmentService.findById(attachment.getId())).willReturn(attachment);

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, attachment.getId(), UserAction.UPDATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, attachment.getId(), UserAction.UPDATE));
    }

    @Test
    void should_set_isRecipient_to_true_for_UPDATE() {
        SecurityUserDetails userDetails = mockUserDetails();
        SecurityGrantedAuthority grantedAuthority = userDetails.getAuthorities().stream().findFirst()
                .orElseThrow(() -> new EtxWebException("Granted Authority not found"));
        MessageSummary messageSummary = EntityTestUtils.mockMessageSummary(
                Message.builder().id(1L).build(),
                grantedAuthority.getGroup()
        );
        messageSummary.getMessage().getMessageSummaries().add(messageSummary);
        Attachment attachment = Attachment.builder().id(1L).message(messageSummary.getMessage()).build();

        given(attachmentService.findById(attachment.getId())).willReturn(attachment);

        policyEnforcement.check(userDetails, attachment.getId(), UserAction.UPDATE);

        verify(policyChecker).check(policyRuleContextArgumentCaptor.capture());
        Map<String, Object> environment = policyRuleContextArgumentCaptor.getValue().getEnvironment();

        assertTrue((Boolean) environment.get("isRecipient"));
    }

    @Test
    void should_set_isRecipient_to_false_for_UPDATE() {
        SecurityUserDetails userDetails = mockUserDetails();
        Group recipient = Group.builder().id(0L).build();
        MessageSummary messageSummary = MessageSummary.builder().recipient(recipient).build();
        Message message = Message.builder().id(1L).build();
        message.getMessageSummaries().add(messageSummary);
        Attachment attachment = Attachment.builder().id(1L).message(message).build();

        given(attachmentService.findById(attachment.getId())).willReturn(attachment);

        policyEnforcement.check(userDetails, attachment.getId(), UserAction.UPDATE);

        verify(policyChecker).check(policyRuleContextArgumentCaptor.capture());
        Map<String, Object> environment = policyRuleContextArgumentCaptor.getValue().getEnvironment();

        assertFalse((Boolean) environment.get("isRecipient"));
    }

    @Test
    void should_evaluate_false_targetId_policy_for_UPDATE_if_attachment_not_found() {
        SecurityUserDetails userDetails = mockUserDetails();
        Long attachmentId = 1L;

        given(attachmentService.findById(attachmentId)).willReturn(null);

        assertFalse(policyEnforcement.check(userDetails, attachmentId, UserAction.UPDATE));
    }
}
