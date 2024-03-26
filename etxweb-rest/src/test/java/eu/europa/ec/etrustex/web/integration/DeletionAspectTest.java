package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.MessageRedirect;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.MessageRedirectRepository;
import eu.europa.ec.etrustex.web.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


class DeletionAspectTest extends AbstractControllerTest {

    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRedirectRepository<MessageRedirect> messageRedirectRepository;



    @Test
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void should_delete_the_redirect_when_the_message_is_deleted() {
        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 2, true, true);

        assertThat(messageRedirectRepository.findByMessageId(messageResult.getMessage().getId())).isNotEmpty();

        messageUserStatusRepository.deleteAll();
        messageService.delete(messageResult.getMessage());

        assertThat(messageRedirectRepository.findByMessageId(messageResult.getMessage().getId())).isEmpty();

    }
}
