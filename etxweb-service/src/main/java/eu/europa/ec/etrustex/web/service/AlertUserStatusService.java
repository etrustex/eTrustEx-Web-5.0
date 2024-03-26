package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;

public interface AlertUserStatusService {

    AlertUserStatus findByUserIdAndAlertIdAndGroupId(Long userId, Long alertId, Long businessId);
    AlertUserStatus create(AlertUserStatusSpec alertUserStatusSpec);
    AlertUserStatus update(AlertUserStatusSpec alertUserStatusSpec);
}
