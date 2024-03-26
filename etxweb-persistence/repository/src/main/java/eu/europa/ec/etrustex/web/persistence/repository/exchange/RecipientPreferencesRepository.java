package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RecipientPreferencesRepository extends CrudRepository<RecipientPreferences, Long> {
    @Modifying
    @Query("delete from RecipientPreferences rp where rp.id = (select g.recipientPreferences.id from Group g where g.id = :groupId)")
    void deleteByGroupId(Long groupId);

}
