package eu.europa.ec.etrustex.web.service.alert;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.repository.AlertUserStatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@AllArgsConstructor
@SuppressWarnings({"squid:S1186"})
public class AlertAspect {

    private final AlertUserStatusRepository alertUserStatusRepository;

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.AlertServiceImpl.save(eu.europa.ec.etrustex.web.exchange.model.AlertSpec)) && args(alertSpec)", argNames = "alertSpec, alert", returning = "alert")
    public void deleteAlertUserStatuses(AlertSpec alertSpec, Alert alert) {
        log.info("About to delete alertUserStatus for all users belong to entity {}", alert.getGroup().getIdentifier());
        alertUserStatusRepository.deleteByGroupId(alert.getGroup().getId());

    }
}
