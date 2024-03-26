package eu.europa.ec.etrustex.web.security.ecas;

import eu.cec.digit.ecas.client.configuration.EcasConfigurationIntf;
import eu.cec.digit.ecas.client.http.HttpRedirector;
import eu.cec.digit.ecas.client.http.LoginParameters;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Slf4j
public class RedirectionInhibitor implements HttpRedirector, Serializable {

    protected static final String UNAUTHORIZED_ACCESS_RESPONSE = "<SOAP-ENV:Fault>\n" +
            "     <faultcode xsi:type=\"xsd:string\">SOAP-ENV:Client</faultcode>\n" +
            "     <faultstring xsi:type=\"xsd:string\">\n" +
            "         Error 401--Unauthorized \n" +
            "        </faultstring>\n" +
            "     <detail xsi:type=\"xsd:string\">\n" +
            "         The request requires user authentication. The response MUST include a WWW-Authenticate header field (section 14.46) containing a challenge applicable to the requested resource. The client MAY repeat the request with a suitable Authorization header field (section 14.8). If the request already included Authorization credentials, then the 401 response indicates that authorization has been refused for those credentials. If the 401 response contains the same challenge as the prior response, and the user agent has already attempted authentication at least once, then the user SHOULD be presented the entity that was given in the response, since that entity MAY include relevant diagnostic information.\n" +
            "        </detail>\n" +
            "</SOAP-ENV:Fault>";


    @Override
    public void setConfiguration(EcasConfigurationIntf configuration) {
        /* no configuration is required for this class, it will always return a 401 unauthorized as redirect */
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, LoginParameters loginParameters) throws IOException {
        log.trace("RedirectionInhibitor.sendRedirect: " + request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (request.getServletPath().equals("/soap/ws")) {
            response.setContentType("application/xml");
            response.getWriter().write(UNAUTHORIZED_ACCESS_RESPONSE);
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}
