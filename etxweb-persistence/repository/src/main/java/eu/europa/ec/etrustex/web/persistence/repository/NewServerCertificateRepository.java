package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewServerCertificateRepository extends JpaRepository<NewServerCertificate, Long> {
}
