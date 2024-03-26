package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.GroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;

import java.util.List;
import java.util.Optional;

public interface GroupConfigurationService {
    <Q, T extends GroupConfiguration<Q>> T updateValue(User user, Long groupId, Class<T> type, GroupConfigurationSpec<Q> groupConfigurationSpec);

    List<GroupConfiguration<?>> findByGroupId(Long groupId);

    GroupConfiguration<?> findByGroupIdAndType(Long groupId, String dtype);

    void saveDefaults(Group group);

    <Q, T extends GroupConfiguration<Q>> Optional<T> findByGroupIdAndType(Long groupId, Class<T> type);

    <Q, T extends GroupConfiguration<Q>> Optional<T> findActiveByGroupIdAndType(Long groupId, Class<T> type);

    <Q, T extends GroupConfiguration<Q>> Q findNonEmptyValueByGroupIdAndType(Long groupId, Class<T> dtype);
}
