package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class FileNameValidatorDelegate {

    private final MessageService messageService;
    private final GroupConfigurationService groupConfigurationService;

    protected <Q, T extends GroupConfiguration<Q>> Pair<T, List<AttachmentSpec>> getConfigurationAndAttachmentSpecs(Object[] args, Class<T> tClass) {

        Long messageId = null;
        SendMessageRequestSpec sendMessageRequestSpec = null;

        for (Object arg : args) {
            if (arg instanceof Long) {
                messageId = (Long) arg;
            } else if (arg instanceof SendMessageRequestSpec) {
                sendMessageRequestSpec = (SendMessageRequestSpec) arg;
            }
        }

        if (messageId == null || sendMessageRequestSpec == null) {
            throw new EtxWebException("Wrong arguments in MessageController.send validation!");
        }

        return Pair.of(getConfiguration(messageId, tClass), sendMessageRequestSpec.getAttachmentSpecs());
    }

    private <Q, T extends GroupConfiguration<Q>> T getConfiguration(Long messageId, Class<T> tClass) {
        Message message = messageService.findById(messageId);

        return groupConfigurationService.findByGroupIdAndType(message.getSenderGroup().getBusinessId(), tClass)
                .orElseThrow(() -> new EtxWebException(String.format("GroupConfiguration not found for business %s and type %s", message.getSenderGroup().getBusinessOrRoot(), tClass.getSimpleName())));
    }
}
