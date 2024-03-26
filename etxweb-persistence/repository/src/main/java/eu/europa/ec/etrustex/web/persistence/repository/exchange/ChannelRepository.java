package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChannelRepository extends CrudRepository<Channel, Long> {
    Page<Channel> findByBusinessIdAndNameContainingIgnoreCase(Long businessId, String name, Pageable pageable);

    int countByBusinessId(Long businessId);

    boolean existsByBusinessIdAndName(Long businessId, String name);

    Channel findByBusinessIdAndName(Long businessId, String name);

    Channel findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);
    List<Channel> findByBusinessIdAndDefaultChannelIsTrue(Long businessId);

    List<Channel> findByBusinessIdAndNameContainingIgnoreCase(Long businessId, String name);

    @Query("SELECT c " +
            "FROM Channel c JOIN ExchangeRule er ON er.channel = c " +
            "WHERE er.member.id = :entityId " +
            "AND c.business.id = :businessId " +
            "ORDER BY c.name ASC")
    List<Channel> findByBusinessIdAndEntityId(Long businessId, Long entityId);

    void deleteByBusiness(Group business);
}
