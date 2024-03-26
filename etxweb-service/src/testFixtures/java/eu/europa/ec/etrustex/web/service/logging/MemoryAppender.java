package eu.europa.ec.etrustex.web.service.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryAppender extends AppenderBase<LoggingEvent> {

    static List<LoggingEvent> list = new ArrayList<>();
    public static boolean contains(String string, Level level) {
        return list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains(string)
                        && event.getLevel().equals(level));
    }

    public static int countEventsForLogger(String loggerName) {
        return (int) list.stream()
                .filter(event -> event.getLoggerName().contains(loggerName))
                .count();
    }

    public static List<LoggingEvent> search(String string) {
        return list.stream()
                .filter(event -> event.getFormattedMessage().contains(string))
                .collect(Collectors.toList());
    }

    public static List<LoggingEvent> search(String string, Level level) {
        return list.stream()
                .filter(event -> event.getFormattedMessage().contains(string)
                        && event.getLevel().equals(level))
                .collect(Collectors.toList());
    }

    public static int getSize() {
        return list.size();
    }

    public static List<LoggingEvent> getLoggedEvents() {
        return Collections.unmodifiableList(list);
    }

    public static void reset() {
        list.clear();
    }

    protected void append(LoggingEvent e) {
        list.add(e);
    }
}