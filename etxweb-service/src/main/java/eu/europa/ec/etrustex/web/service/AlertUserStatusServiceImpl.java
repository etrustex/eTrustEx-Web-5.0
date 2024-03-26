package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.AlertUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertUserStatusServiceImpl implements AlertUserStatusService{

    private final AlertUserStatusRepository alertUserStatusRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    @Override
    public AlertUserStatus findByUserIdAndAlertIdAndGroupId(Long userId, Long alertId, Long businessId) {
        return alertUserStatusRepository.findByUserIdAndAlertIdAndGroupId(userId, alertId, businessId).orElse(null);
    }

    @Override
    public AlertUserStatus create(AlertUserStatusSpec alertUserStatusSpec) {
        return alertUserStatusRepository.save(toAlertUserStatus(alertUserStatusSpec));
    }

    @Override
    public AlertUserStatus update(AlertUserStatusSpec alertUserStatusSpec) {
        AlertUserStatus alertUserStatus = alertUserStatusRepository.findByUserIdAndAlertIdAndGroupId(alertUserStatusSpec.getUserId(), alertUserStatusSpec.getAlertId(), alertUserStatusSpec.getGroupId())
                .orElseThrow(() -> new EtxWebException(String.format("AlertUserStatus not found to update AlertUserStatus for userId:%S, alertId:%s, groupId:%s",
                        alertUserStatusSpec.getUserId(), alertUserStatusSpec.getAlertId(), alertUserStatusSpec.getGroupId())));

        alertUserStatus.setStatus(AlertUserStatus.AlertUserStatusType.valueOf(alertUserStatusSpec.getStatus()));
        return alertUserStatusRepository.save(alertUserStatus);
    }

    AlertUserStatus toAlertUserStatus(AlertUserStatusSpec alertUserStatusSpec) {
        Alert alert = alertRepository.findById(alertUserStatusSpec.getAlertId())
                .orElseThrow(() -> new EtxWebException(String.format("Alert not found for groupId: %s", alertUserStatusSpec.getGroupId())));

        User user = userRepository.findById(alertUserStatusSpec.getUserId())
                .orElseThrow(() -> new EtxWebException("User not found to update AlertUserStatus"));

        return AlertUserStatus.builder()
                .alert(alert)
                .status(AlertUserStatus.AlertUserStatusType.valueOf(alertUserStatusSpec.getStatus()))
                .user(user)
                .groupId(alertUserStatusSpec.getGroupId())
                .build();
    }
}
