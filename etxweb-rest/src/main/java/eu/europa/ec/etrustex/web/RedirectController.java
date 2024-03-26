package eu.europa.ec.etrustex.web;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.REDIRECT;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;

    @GetMapping(REDIRECT)
    public RedirectView redirectTo(@PathVariable UUID redirectId) {
        String redirectUrl = redirectService.getTargetUrl(redirectId);
        return getRedirectView(redirectUrl);
    }

    private RedirectView getRedirectView(String url) {
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(false);
        redirectView.setUrl(url);
        redirectView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirectView;
    }
}
