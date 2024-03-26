package eu.europa.ec.etrustex.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {
    public static final String REDIRECT_PREFIX = "redirect:#/";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("javax.servlet.forward.request_uri");

        if (statusCode.equals(HttpStatus.FORBIDDEN.value())) {
            throw new AccessDeniedException("Access forbidden!");
        }

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            log.info("Not found {}. ", requestUri);
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("Internal Server Error {}. ", requestUri);
        }

        return REDIRECT_PREFIX + requestUri;
    }
}
