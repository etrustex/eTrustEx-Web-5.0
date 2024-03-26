package eu.europa.ec.etrustex.web.rest.logging;

import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AlertService;
import eu.europa.ec.etrustex.web.service.ChannelService;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String LOG_OBJECT_KEY = "LOG_OBJECT_KEY";
    public static final String API_PREFIX = "/api/v1/";

    private static final Map<String, LogRenderingEnum> logMap = new HashMap<>();

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GrantedAuthorityService grantedAuthorityService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ExchangeRuleService exchangeRuleService;

    static {
        Arrays.stream(LogRenderingEnum.values()).forEach(logRenderingEnum -> logMap.put(logRenderingEnum.getMapKey(), logRenderingEnum));
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        ControllerLoggingItem.ControllerLoggingItemBuilder controllerLoggingItemBuilder = ControllerLoggingItem.builder();
        boolean emptyUser = true;

        if (request.getUserPrincipal() instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = (PreAuthenticatedAuthenticationToken) request.getUserPrincipal();
            if (preAuthenticatedAuthenticationToken.getPrincipal() instanceof SecurityUserDetails) {
                SecurityUserDetails securityUserDetails = (SecurityUserDetails) preAuthenticatedAuthenticationToken.getPrincipal();
                controllerLoggingItemBuilder
                        .userId(securityUserDetails.getUser().getEcasId());
                emptyUser = false;
            }
        }

        controllerLoggingItemBuilder
                .methodStartTimestamp(Calendar.getInstance().getTime())
                .referer(request.getHeader("referer"))
                .userAgent(request.getHeader("user-agent"))
                .userIp(request.getHeader("x-forwarded-for"))
                .requestURL(request.getRequestURI())
                .serverName(request.getServerName())
                .serverPort(request.getServerPort())
                .requestMethod(request.getMethod())
        ;

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            controllerLoggingItemBuilder
                    .statusCode(response.getStatus())
                    .controllerClass(handlerMethod.getBeanType().getName())
                    .methodCalled(handlerMethod.getMethod().getName())
            ;

            controllerLoggingItemBuilder.parameters(getRequestParameters(request));

            controllerLoggingItemBuilder.headers(getRequestHeaders(request));
        }

        if (!emptyUser) {
            request.setAttribute(LOG_OBJECT_KEY, controllerLoggingItemBuilder);
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        ControllerLoggingItem.ControllerLoggingItemBuilder controllerLoggingItemBuilder = retrieveLogItem(request);

        if (controllerLoggingItemBuilder == null) {
            return;
        }

        if (ex != null) {
            controllerLoggingItemBuilder.successfulCall(false);
            controllerLoggingItemBuilder.errorMessage(ex.getMessage());
        }

        if (response.getStatus() == 500 || response.getStatus() == 400) {
            controllerLoggingItemBuilder.successfulCall(false);
        }


        controllerLoggingItemBuilder.headers(getRequestHeaders(request));

        controllerLoggingItemBuilder.statusCode(response.getStatus());

        controllerLoggingItemBuilder.messageService(messageService);
        controllerLoggingItemBuilder.groupService(groupService);
        controllerLoggingItemBuilder.grantedAuthorityService(grantedAuthorityService);
        controllerLoggingItemBuilder.alertService(alertService);
        controllerLoggingItemBuilder.userService(userService);
        controllerLoggingItemBuilder.channelService(channelService);
        controllerLoggingItemBuilder.exchangeRuleService(exchangeRuleService);

        renderLog(controllerLoggingItemBuilder.build());
    }


    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();

        Enumeration<?> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String curr = (String) e.nextElement();
            parameters.put(curr, request.getParameter(curr));
        }

        return parameters;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String curr = headerNames.nextElement();
            headers.put(curr, request.getHeader(curr));
        }

        return headers;
    }

    private ControllerLoggingItem.ControllerLoggingItemBuilder retrieveLogItem(HttpServletRequest request) {
        ControllerLoggingItem.ControllerLoggingItemBuilder controllerLoggingItemBuilder = null;

        if (request.getAttribute(LOG_OBJECT_KEY) instanceof ControllerLoggingItem.ControllerLoggingItemBuilder) {
            controllerLoggingItemBuilder = (ControllerLoggingItem.ControllerLoggingItemBuilder) request.getAttribute(LOG_OBJECT_KEY);
            request.removeAttribute(LOG_OBJECT_KEY);
        }

        return controllerLoggingItemBuilder;
    }

    private void renderLog(ControllerLoggingItem controllerLoggingItem) {
        if (logMap.containsKey(controllerLoggingItem.getMapKey())) {
            LogRenderingEnum logRenderingEnum = logMap.get(controllerLoggingItem.getMapKey());
            String logString = logRenderingEnum.renderLog(controllerLoggingItem);
            if (StringUtils.isNotEmpty(logString)) {
                if (logRenderingEnum.name().startsWith("ADMIN_")) {
                    AdminLog.log.info(logString);
                } else {
                    WebLog.log.info(logString);
                }
            }
        } else {
            WebLog.log.info("Generic:" + controllerLoggingItem);
        }
    }

    @Slf4j
    static class WebLog {
    }

    @Slf4j
    static class AdminLog {
    }
}
