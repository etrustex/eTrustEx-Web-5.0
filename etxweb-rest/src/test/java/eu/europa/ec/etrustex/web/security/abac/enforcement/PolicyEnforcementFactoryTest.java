package eu.europa.ec.etrustex.web.security.abac.enforcement;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.security.abac.enforcement.implementation.AttachmentPolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.implementation.MessagePolicyEnforcement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PolicyEnforcementFactoryTest {
    private static PolicyEnforcementFactory policyEnforcementFactory;

    @BeforeAll
    public static void init() {
        AttachmentPolicyEnforcement attachmentPolicyEnforcement = mock(AttachmentPolicyEnforcement.class);
        MessagePolicyEnforcement messagePolicyEnforcement = mock(MessagePolicyEnforcement.class);

        given(attachmentPolicyEnforcement.getDomainType()).willReturn(Attachment.class.getSimpleName());
        given(messagePolicyEnforcement.getDomainType()).willReturn(Message.class.getSimpleName());

        List<PolicyEnforcement> policyEnforcements = Arrays.asList(attachmentPolicyEnforcement, messagePolicyEnforcement);
        policyEnforcementFactory = new PolicyEnforcementFactory(policyEnforcements);
    }

    @Test
    void should_get_policy_enforcement() {
        policyEnforcementFactory.initPermissionEvaluatorsCache();

        assertThat(PolicyEnforcementFactory.getPolicyEnforcement(Attachment.class.getSimpleName())).isInstanceOf(AttachmentPolicyEnforcement.class);
        assertThat(PolicyEnforcementFactory.getPolicyEnforcement(Message.class.getSimpleName())).isInstanceOf(MessagePolicyEnforcement.class);
    }

    @Test
    void should_throw_if_policy_enforcement_not_found() {
        assertThrows(EtxWebException.class, () -> PolicyEnforcementFactory.getPolicyEnforcement("Foo"));
    }
}
