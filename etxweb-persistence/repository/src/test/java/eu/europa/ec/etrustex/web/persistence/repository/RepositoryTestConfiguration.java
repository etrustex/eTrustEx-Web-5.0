package eu.europa.ec.etrustex.web.persistence.repository;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "eu.europa.ec.etrustex.web.persistence.entity")
public class RepositoryTestConfiguration  {
}
