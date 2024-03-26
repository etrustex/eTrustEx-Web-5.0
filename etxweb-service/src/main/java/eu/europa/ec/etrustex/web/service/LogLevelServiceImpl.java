package eu.europa.ec.etrustex.web.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import eu.europa.ec.etrustex.web.exchange.command.Command;
import eu.europa.ec.etrustex.web.exchange.command.CommandProperty;
import eu.europa.ec.etrustex.web.exchange.model.LogLevelItem;
import eu.europa.ec.etrustex.web.service.jms.JmsSender;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogLevelServiceImpl implements LogLevelService {

    private final JmsSender jmsSender;

    @Override
    public Page<LogLevelItem> findLoggers(String search, PageRequest pageRequest) {
        log.info("Getting the loggers with the search '{}'", search);

        List<LogLevelItem> levels = new ArrayList<>();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        List<Logger> nativeLoggerList = loggerContext.getLoggerList().stream().
                sorted(Comparator.comparing(Logger::getName))
                .collect(Collectors.toList());

        if(!StringUtils.isEmpty(search)){
            nativeLoggerList = nativeLoggerList.stream()
                    .filter(logger -> logger.getName().contains(search))
                    .sorted(Comparator.comparing(Logger::getName))
                    .collect(Collectors.toList());
        }

        //Mapping
        for (Logger logger : nativeLoggerList) {
            levels.add(LogLevelItem.builder().loggerName(logger.getName()).loggerLevel(logger.getEffectiveLevel().toString()).build());
        }

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), levels.size());

        List<LogLevelItem> pageContent = levels.subList(start, end);

        return new PageImpl<>(pageContent, pageRequest, levels.size());
    }

    @Override
    public void setLoggingLevel(String logger, String level) {
        log.info("Setting the Logger '{}' on the level '{}'", logger, level);
        //get the level from the string value
        Level levelObj = toLevel(level);

        //get the logger context
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        loggerContext.getLogger(logger).setLevel(levelObj);
    }

    @Override
    public void resetLogging() {
        log.info("Resetting the logging to the Default values'");
        //we are re-using the same service used at context initialization
        final InputStream logbackConfigurationFile = this.getClass().getClassLoader().getResourceAsStream("logback.xml");

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackConfigurationFile);
        } catch (JoranException je) {
            log.error("Problem resetting the logs");
        } finally {
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        }
    }
    @Override
    public void signalResetLogging() {
        Map<String, String> commandProperties = new HashMap<>();
        commandProperties.put(Command.COMMAND_NAME, Command.LOGGING_RESET);
        jmsSender.sendCommand(commandProperties);
    }

    @Override
    public void signalLoggingSetLevel(String logger, String level) {
        Map<String, String> commandProperties = new HashMap<>();
        commandProperties.put(Command.COMMAND_NAME, Command.LOGGING_SET_LEVEL);
        commandProperties.put(CommandProperty.LOG_NAME, logger);
        commandProperties.put(CommandProperty.LOG_LEVEL, level);
        jmsSender.sendCommand(commandProperties);
    }

    private Level toLevel(String logLevel) {
        final EtxWebException loggingException = new EtxWebException("Not a known log level: [" + logLevel + "]");

        if (org.apache.commons.lang3.StringUtils.isBlank(logLevel)) {
            throw loggingException;
        }
        switch (logLevel.toUpperCase()) {
            case "ALL":
                return Level.ALL;
            case "TRACE":
                return Level.TRACE;
            case "DEBUG":
                return Level.DEBUG;
            case "INFO":
                return Level.INFO;
            case "WARN":
                return Level.WARN;
            case "ERROR":
                return Level.ERROR;
            case "OFF":
                return Level.OFF;

            default:
                throw loggingException;
        }
    }
}
