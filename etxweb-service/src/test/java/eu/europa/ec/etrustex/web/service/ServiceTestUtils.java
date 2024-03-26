package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ServiceTestUtils {

    private static final Random RANDOM = new SecureRandom();

    private ServiceTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static MessageSummarySpec mockMessageSummarySpec() {
        return mockMessageSummarySpec(null);
    }

    public static MessageSummarySpec mockMessageSummarySpec(Long recipientGroupId) {
        MessageSummarySpec messageSummarySpec = new MessageSummarySpec();
        messageSummarySpec.setRecipientId(recipientGroupId);
        return messageSummarySpec;
    }

    public static GroupSpec mockValidationGroupSpec(Group group) {
        return GroupSpec.builder()
                .identifier(group.getIdentifier())
                .parentGroupId(group.getParent().getId())
                .displayName(group.getName())
                .description(group.getDescription())
                .recipientPreferencesId(group.getRecipientPreferences() != null ? group.getRecipientPreferences().getId() : RANDOM.nextLong())
                .senderPreferencesId(group.getSenderPreferences() != null ? group.getSenderPreferences().getId() : RANDOM.nextLong())
                .isActive(group.isActive())
                .type(group.getType())
                .build();
    }

    public static SendMessageRequestSpec mockSendMessageRequest(String subject, MessageSummarySpec... messageSummarySpecs) {
        List<MessageSummarySpec> messageSummarySpecList = new ArrayList<>();
        Collections.addAll(messageSummarySpecList, messageSummarySpecs);

        SendMessageRequestSpec sendMessageRequestSpec = SendMessageRequestSpec.builder()
                .recipients(messageSummarySpecList)
                .subject(subject)
                .attachmentsTotalByteLength(1234L)
                .attachmentTotalNumber(3)
                .text("Test body")
                .attachmentSpecs(null)
                .templateVariables("ZqeqaN7AfDuvak+GO54h8ahlYwB494n+Buv1Zo/oHtQRAetzVgrvYKin6HYpFjvs2yV60ZjkB7IU/VLtXXSYx13ZGNRlLvu37B9/AoZY04JSpt3RcfA7Ztv015L0PQQug3S5ICPFVA1NHrwQbG/dB4g21fYuOIO/R+9q40JHR3phPVxFxPsDFjHd8+XnBDOAIVpFX26wVq6x7ag6LLGmcgawcB6WrZVW0fc/NCfGd8f8yA1mHmP6vuejRNSvleNZNLyBS2Cr1bTm/YvAAbnq9RABNqaTb1rFcdXCyTn769jZGRm5amfir4n3+nluioMnbxhM6mA7yksLoU5OGvYiQe5gejIqrbg=")
                .build();

        sendMessageRequestSpec.setRecipients(messageSummarySpecList);

        return sendMessageRequestSpec;
    }


}
