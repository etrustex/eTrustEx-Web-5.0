package eu.europa.ec.etrustex.web.exchange.command;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

public class Command {
    public static final String COMMAND_NAME = "COMMAND_NAME";
    public static final String LOGGING_RESET = "LOGGING_RESET";
    public static final String LOGGING_SET_LEVEL = "LOGGING_SET_LEVEL";
    private Command() { throw new EtxWebException("Utility class"); }
}
