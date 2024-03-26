package eu.europa.ec.etrustex.web.security.ecas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static eu.europa.ec.etrustex.web.security.ecas.RedirectionInhibitor.UNAUTHORIZED_ACCESS_RESPONSE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedirectionInhibitorTest {

    @Spy
    private HttpServletRequest httpServletRequest;
    @Spy
    private HttpServletResponse httpServletResponse;
    RedirectionInhibitor redirectionInhibitor;

    @BeforeEach
    public void init() {
        given(httpServletRequest.getRequestURI()).willReturn("http://an/uri");
        redirectionInhibitor = new RedirectionInhibitor();
    }

    @Test
    void should_set_to_unauthorized() throws IOException {

        given(httpServletRequest.getServletPath()).willReturn("/not/soap/ws");
        redirectionInhibitor.setConfiguration(null); // done only for coverage

        redirectionInhibitor.sendRedirect(httpServletRequest, httpServletResponse, null);

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }

    @Test
    void should_set_the_content_type_and_write_to_the_writer() throws IOException {
        PrintWriter printWriter = mock(PrintWriter.class);

        given(httpServletRequest.getServletPath()).willReturn("/soap/ws");
        given(httpServletResponse.getWriter()).willReturn(printWriter);

        redirectionInhibitor.sendRedirect(httpServletRequest, httpServletResponse, null);

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(httpServletResponse).setContentType("application/xml");
        verify(printWriter).write(UNAUTHORIZED_ACCESS_RESPONSE);
    }
}
