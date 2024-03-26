package eu.europa.ec.etrustex.web.rest.config;

import eu.europa.ec.etrustex.web.filter.LoggingForwardedHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class ForwardedHeaderFilterConfig {
    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new LoggingForwardedHeaderFilter();
    }
}
