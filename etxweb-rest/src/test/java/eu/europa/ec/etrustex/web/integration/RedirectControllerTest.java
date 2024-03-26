package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RedirectControllerTest extends AbstractControllerTest {
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private RedirectService redirectService;

    @Autowired
    private RedirectRepository redirectRepository;

    @Test
    void should_redirect() throws Exception {
        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 1, true, true);

        Redirect redirect = redirectRepository.findByGroupId(
                        messageResult.getRecipientEntities().get(0)
                                .getId())
                .iterator().next();

        String redirectUrl = redirectService.getTargetUrl(redirect.getId());

        MvcResult mvcResult = mockMvc.perform(
                        get(REDIRECT, redirect.getId().toString())
                )
                .andExpect(status().isTemporaryRedirect()).andReturn();

        String receivedRedirectURL = mvcResult.getResponse().getHeader("Location");

        assertEquals(redirectUrl, receivedRedirectURL);

    }
}

