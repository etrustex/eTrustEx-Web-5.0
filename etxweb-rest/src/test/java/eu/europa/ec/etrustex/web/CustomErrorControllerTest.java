package eu.europa.ec.etrustex.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;

import static eu.europa.ec.etrustex.web.CustomErrorController.REDIRECT_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    private static final String JAVA_SERVLET_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    private static final String REQUEST_URI = "some-uri";

    private ListAppender<ILoggingEvent> logWatcher;

    @Mock
    private HttpServletRequest httpServletRequest;

    private CustomErrorController customErrorController;


    @BeforeEach
    public void init() {
        customErrorController = new CustomErrorController();

        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(CustomErrorController.class)).addAppender(logWatcher);

        given(httpServletRequest.getAttribute("javax.servlet.forward.request_uri")).willReturn(REQUEST_URI);
    }


    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(CustomErrorController.class)).detachAndStopAllAppenders();
    }



    @Test
    void should_throw_access_denied() {
        given(httpServletRequest.getAttribute(JAVA_SERVLET_ERROR_STATUS_CODE)).willReturn(HttpStatus.FORBIDDEN.value());
        assertThrows(AccessDeniedException.class, () -> customErrorController.handleError(httpServletRequest));
    }

    @Test
    void should_return_error() {
        given(httpServletRequest.getAttribute(JAVA_SERVLET_ERROR_STATUS_CODE)).willReturn(HttpStatus.TEMPORARY_REDIRECT.value());

        assertEquals(REDIRECT_PREFIX + REQUEST_URI, customErrorController.handleError(httpServletRequest));
    }

    @Test
    void should_return_error_404() {
        given(httpServletRequest.getAttribute(JAVA_SERVLET_ERROR_STATUS_CODE)).willReturn(HttpStatus.NOT_FOUND.value());

        assertEquals(REDIRECT_PREFIX + REQUEST_URI, customErrorController.handleError(httpServletRequest));

        ILoggingEvent loggingEvent = logWatcher.list.get(0);
        assertEquals(Level.INFO, loggingEvent.getLevel());
        assertThat(loggingEvent.getFormattedMessage()).contains(String.format("Not found %s.", REQUEST_URI));
    }

    @Test
    void should_return_error_500() {
        given(httpServletRequest.getAttribute(JAVA_SERVLET_ERROR_STATUS_CODE)).willReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertEquals(REDIRECT_PREFIX + REQUEST_URI, customErrorController.handleError(httpServletRequest));

        ILoggingEvent loggingEvent = logWatcher.list.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertThat(loggingEvent.getFormattedMessage()).contains(String.format("Internal Server Error %s.", REQUEST_URI));
    }
}
