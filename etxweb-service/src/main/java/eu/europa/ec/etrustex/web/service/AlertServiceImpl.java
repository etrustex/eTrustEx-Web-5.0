package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final GroupRepository groupRepository;

    @Override
    public List<Alert> findActiveByGroupIdAndDate(Long groupId, Date date) {
        return alertRepository.findActiveByGroupIdAndDate(groupId, date);
    }

    @Override
    public Optional<Alert> findByGroupId(Long groupId) {
        return alertRepository.findByGroupId(groupId);
    }

    @Override
    @Transactional
    public Alert save(AlertSpec alertSpec) {
        return alertRepository.save(toAlert(alertSpec));
    }

    @Override
    public Alert findLastUpdatedAlert(String user) {
        return alertRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(user);
    }

    private Alert toAlert(AlertSpec alertSpec) {
        Group businessOrRoot = groupRepository.findById(alertSpec.getGroupId())
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find business with id: %s", alertSpec.getGroupId())));

        Alert alert = alertRepository.findByGroupId(alertSpec.getGroupId())
                .orElseGet(Alert::new);

        alert.setGroup(businessOrRoot);
        alert.setActive(alertSpec.isActive());
        alert.setContent(alertSpec.getContent());
        alert.setTitle(alertSpec.getTitle());
        alert.setType(alertSpec.getType());
        alert.setStartDate(alertSpec.getStartDate());
        alert.setEndDate(alertSpec.getEndDate());

        return alert;

    }
}
