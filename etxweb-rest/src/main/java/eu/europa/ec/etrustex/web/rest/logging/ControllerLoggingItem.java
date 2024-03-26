package eu.europa.ec.etrustex.web.rest.logging;

import eu.europa.ec.etrustex.web.service.AlertService;
import eu.europa.ec.etrustex.web.service.ChannelService;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Builder
@Getter
@Setter
public class ControllerLoggingItem {

    private String userId;

    private Date methodStartTimestamp;

    private String serverName;
    private int serverPort;
    private String referer;
    private String requestURL;
    private String requestMethod;

    private String userIp;
    private String userAgent;

    private String controllerClass;
    private String methodCalled;
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();

    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    @Builder.Default
    private boolean successfulCall = true;
    private String errorMessage;
    private int statusCode;

    private MessageService messageService;
    private GroupService groupService;
    private GrantedAuthorityService grantedAuthorityService;
    private AlertService alertService;
    private UserService userService;
    private ChannelService channelService;
    private ExchangeRuleService exchangeRuleService;

    @Override
    public String toString() {
        return new StringJoiner(", ", "", "")
                .add("" + methodStartTimestamp)
                .add("userId='" + userId + "'")
                .add("serverName='" + serverName + "'")
                .add("serverPort=" + serverPort)
                .add("referer='" + referer + "'")
                .add("requestURL='" + requestURL + "'")
                .add("requestMethod='" + requestMethod + "'")
                .add("userIp='" + userIp + "'")
                .add("userAgent='" + userAgent + "'")
                .add("controllerClass='" + controllerClass + "'")
                .add("methodCalled='" + methodCalled + "'")
                .add("parameters=" + parameters)
                .add("successfulCall=" + successfulCall)
                .add("errorMessage='" + errorMessage + "'")
                .add("statusCode=" + statusCode)
                .toString();
    }

    public String getMapKey() {
        return controllerClass + "." + methodCalled + "." + requestMethod;
    }

    public String getBeforeLastURLElement() {
        String url = getRequestURL().substring(getRequestURL().indexOf(LoggingInterceptor.API_PREFIX));
        int from = StringUtils.ordinalIndexOf(url, "/", 4);
        return url.substring(from + 1, StringUtils.ordinalIndexOf(url, "/", 5));
    }

    public Long getLastURLElement() {
        String url = getRequestURL().substring(getRequestURL().indexOf(LoggingInterceptor.API_PREFIX));
        return Long.valueOf(url.substring(url.lastIndexOf('/') + 1));
    }
}
