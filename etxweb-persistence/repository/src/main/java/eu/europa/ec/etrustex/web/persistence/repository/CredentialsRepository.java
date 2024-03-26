package eu.europa.ec.etrustex.web.persistence.repository;


import eu.europa.ec.etrustex.web.persistence.entity.Credentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Credentials findCredentialsByUserName(String userName);
}
