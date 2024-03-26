package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.GroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.RetentionPolicyGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.IntegerGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.StringGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration.GroupConfigurationRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.notification.NotificationService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupConfigurationServiceImpl implements GroupConfigurationService {

    private final GroupConfigurationRepository<GroupConfiguration<?>> groupConfigurationRepository;
    private final GroupRepository groupRepository;
    private final NotificationService notificationService;

    @Override
    public <Q, T extends GroupConfiguration<Q>> T updateValue(User user, Long groupId, Class<T> type, GroupConfigurationSpec<Q> groupConfigurationSpec) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new EtxWebException(String.format("Group not found with id %s", groupId)));
        T groupConfiguration = groupConfigurationRepository.findFirstByGroupId(groupId, type.getSimpleName(), type)
                .orElseThrow(() -> new EtxWebException(String.format("GroupConfiguration not found for group with id %s and dtype %s", groupId, type.getSimpleName())));

        if (groupConfiguration instanceof RetentionPolicyGroupConfiguration && groupConfiguration.isActive() && groupConfigurationSpec.isActive()) {
            int previousValue = ((RetentionPolicyGroupConfiguration) groupConfiguration).getIntegerValue();
            int newValue = ((RetentionPolicyGroupConfigurationSpec) groupConfigurationSpec).getValue();
            notificationService.notifyOfRetentionPolicyChanged(user, group, previousValue, newValue);
        }

        groupConfiguration.setValue(groupConfigurationSpec.getValue());
        groupConfiguration.setActive(groupConfigurationSpec.isActive());

        return groupConfigurationRepository.save(groupConfiguration);
    }

    @Override
    public List<GroupConfiguration<?>> findByGroupId(Long groupId) {
        return groupConfigurationRepository.findByGroupId(groupId);
    }

    @Override
    public GroupConfiguration<?> findByGroupIdAndType(Long groupId, String dtype) {
        return groupConfigurationRepository.findFirstByGroupIdAndDtype(groupId, dtype)
                .orElseThrow(() -> new EtxWebException(String.format("GroupConfiguration not found for group with id %s and dtype %s", groupId, dtype)));
    }

    @Override
    public void saveDefaults(Group business) {
        @SuppressWarnings("rawtypes")
        List<GroupConfiguration> groupConfigurations = GroupConfigurationFactory.defaultGroupConfigurations(business);
        groupConfigurationRepository.saveAll(groupConfigurations);
    }


    @Override
    public <Q, T extends GroupConfiguration<Q>> Optional<T> findByGroupIdAndType(Long groupId, Class<T> type) {
        return groupConfigurationRepository.findFirstByGroupId(groupId, type.getSimpleName(), type);
    }


    @Override
    public <Q, T extends GroupConfiguration<Q>> Optional<T> findActiveByGroupIdAndType(Long groupId, Class<T> type) {
        return groupConfigurationRepository.findFirstActiveByGroupId(groupId, type.getSimpleName(), type);
    }

    @Override
    public <Q, T extends GroupConfiguration<Q>> Q findNonEmptyValueByGroupIdAndType(Long groupId, Class<T> dtype) {
        final Object[] results = {null};
        if (groupId != null) {
            groupConfigurationRepository.findFirstByGroupIdAndDtype(groupId, dtype.getSimpleName())
                    .filter(GroupConfiguration::isActive)
                    .ifPresent(groupConfiguration -> {
                        if (groupConfiguration instanceof StringGroupConfiguration) {
                            String value = ((StringGroupConfiguration) groupConfiguration).getStringValue();
                            if (StringUtils.isNotEmpty(value)) {
                                results[0] = value;
                            }
                        } else if (groupConfiguration instanceof IntegerGroupConfiguration) {
                            Integer value = ((IntegerGroupConfiguration) groupConfiguration).getIntegerValue();
                            if ((value != null) && (value != -1)) {
                                results[0] = value;
                            }
                        }
                    });
        }

        @SuppressWarnings("unchecked")
        Q result = (Q) results[0];

        return result;
    }

}
