package eu.europa.ec.etrustex.web.persistence.repository.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"eu.europa.ec.etrustex.web.persistence.entity"})
public class PersistenceConfig {
}
