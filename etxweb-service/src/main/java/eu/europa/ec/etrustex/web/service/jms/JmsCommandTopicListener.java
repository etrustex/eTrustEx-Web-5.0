package eu.europa.ec.etrustex.web.service.jms;

import eu.europa.ec.etrustex.web.exchange.command.Command;
import eu.europa.ec.etrustex.web.exchange.command.CommandProperty;
import eu.europa.ec.etrustex.web.service.LogLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static eu.europa.ec.etrustex.web.service.jms.config.JmsConfig.COMMAND_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsCommandTopicListener {

    private final LogLevelService logLevelService;

    @JmsListener(destination = COMMAND_TOPIC, containerFactory = "nonTransactedListenerFactory")
    public void onMessage(Map<String, String> commandProperties) {
        log.info("About to handle the command {}",commandProperties);
        try {
            if(Command.LOGGING_RESET.equalsIgnoreCase(commandProperties.get(Command.COMMAND_NAME))){
                logLevelService.resetLogging();
            } else if (Command.LOGGING_SET_LEVEL.equalsIgnoreCase(commandProperties.get(Command.COMMAND_NAME))) {
                logLevelService.setLoggingLevel(commandProperties.get(CommandProperty.LOG_NAME), commandProperties.get(CommandProperty.LOG_LEVEL));
            }
        } catch (Exception e) {
            log.error("Problem treating the command "+commandProperties,e);
        }
    }
}
