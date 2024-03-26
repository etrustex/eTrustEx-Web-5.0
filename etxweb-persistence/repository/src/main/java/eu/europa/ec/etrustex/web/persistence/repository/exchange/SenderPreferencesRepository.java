package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SenderPreferencesRepository extends CrudRepository<SenderPreferences, Long> {
    @Query("select count(g) from Group g where g.senderPreferences.id = :senderPreferencesId")
    int countGroupsById(Long senderPreferencesId);
    @Modifying
    @Query("delete from SenderPreferences sp where sp.id = (select g.senderPreferences.id from Group g where g.id = :groupId)")
    void deleteByGroupId(Long groupId);
}
