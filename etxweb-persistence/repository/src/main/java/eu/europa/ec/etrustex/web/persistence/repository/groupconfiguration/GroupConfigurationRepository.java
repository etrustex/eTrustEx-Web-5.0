package eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupConfigurationRepository<T extends GroupConfiguration<?>> extends CrudRepository<T, Long> {
    List<T> findByGroupId(Long groupId);

    Optional<T> findFirstByGroupIdAndDtype(Long groupId, String dtype);

    @Query("select distinct c from GroupConfiguration c where c.group.id = :groupId  and c.dtype = :dtype")
    <Q extends T> Optional<Q> findFirstByGroupId(@Param("groupId") Long groupId, @Param("dtype") String dtype, Class<Q> qClass);

    @Query("select distinct c from GroupConfiguration c where c.group.id = :groupId  and c.dtype = :dtype and c.active = true")
    <Q extends T> Optional<Q> findFirstActiveByGroupId(@Param("groupId") Long groupId, @Param("dtype") String dtype, Class<Q> qClass);

    void deleteByGroupId(Long groupId);
}
