package eu.europa.ec.etrustex.web.monitoring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**
 * Monitoring the execution times of all methods annotated as TimeLogged
 * plus all methods in the service package.
 *
 * org.springframework.aop.interceptor.PerformanceMonitorInterceptor level below trace will disable logging of execution times
 */
//@Configuration
@EnableAspectJAutoProxy
@Aspect
@SuppressWarnings({"squid:S1186"})
public class MonitoringConfig {

    @Pointcut("execution(* eu.europa.ec.etrustex.web.service.*.*(..)) && args(..)")
    public void monitor() {}

    @Pointcut(value =
            "(execution(public * *(..)) && @within(eu.europa.ec.etrustex.web.monitoring.TimeLogged))" +
            "|| @annotation(eu.europa.ec.etrustex.web.monitoring.TimeLogged)")
    public void timeLogged() {}

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(false);
    }

    @Bean
    public Advisor performanceMonitorAdvisor(PerformanceMonitorInterceptor performanceMonitorInterceptor) {
        AspectJExpressionPointcut pointcut= new AspectJExpressionPointcut();
        pointcut.setExpression(
                "MonitoringConfig.timeLogged()" +
                "|| MonitoringConfig.monitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor);
    }
}
