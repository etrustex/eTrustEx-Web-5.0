package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AlertService {

    /**
     * @param groupId id of the business of which we want the active alert or ROOT for system-wide alerts.
     * @return the alert for the business and alert for ROOT (system) if existing and active at the current time, otherwise empty list
     */
    List<Alert> findActiveByGroupIdAndDate(Long groupId, Date date);

    /**
     * @param groupId id of the business of which we want the alert.
     * @return the alert for the business or for ROOT (system) if exist. Otherwise Optional.empty()
     */
    Optional<Alert> findByGroupId(Long groupId);

    Alert save(AlertSpec alertSpec);

    Alert findLastUpdatedAlert(String user);
}
