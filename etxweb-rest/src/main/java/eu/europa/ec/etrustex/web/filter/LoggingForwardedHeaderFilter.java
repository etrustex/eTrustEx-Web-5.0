package eu.europa.ec.etrustex.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoggingForwardedHeaderFilter extends ForwardedHeaderFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        this.logRequest(request);
        super.doFilterInternal(request, response, filterChain);
    }

    private void logRequest(HttpServletRequest request) {
        log.info("***");
        log.info(request.getRequestURI());
        log.info(request.getMethod());
        logHeader(request, "origin");
        logHeader(request, "referer");
        logHeader(request, "forwarded");
        logHeader(request, "x-forwarded-host");
        logHeader(request, "x-forwarded-proto");
        logHeader(request, "x-forwarded-port");
        logHeader(request, "x-forwarded-for");
        log.info("Scheme: {}", request.getScheme());
        log.info("ServerName: {}", request.getServerName());
        log.info("ServerPort: {}", request.getServerPort());
    }
    private void logHeader(HttpServletRequest request, String header) {
        log.info("{}: {}", header, request.getHeader(header));
    }
}
