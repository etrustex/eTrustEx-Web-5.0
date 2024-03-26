package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.LogLevelItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface LogLevelService {

    Page<LogLevelItem> findLoggers(String filterValue, PageRequest pageRequest);
    void setLoggingLevel(String logger, String level);
    void resetLogging();
    void signalResetLogging();
    void signalLoggingSetLevel(String logger, String level);
}
