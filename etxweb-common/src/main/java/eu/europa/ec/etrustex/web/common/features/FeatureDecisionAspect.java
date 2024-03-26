package eu.europa.ec.etrustex.web.common.features;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class FeatureDecisionAspect {
    private final EtrustexWebProperties etrustexWebProperties;

    @Around("@annotation(eu.europa.ec.etrustex.web.common.features.FeatureDecision)")
    public Object checkFeature(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        FeatureDecision featureDecision = method.getAnnotation(FeatureDecision.class);

        if( ! featureDecision.value().isActive(etrustexWebProperties.getEnvironment()) ) {
            log.info("Feature not enabled: " + featureDecision.value());
            return null;
        }

        return pjp.proceed();
    }
}
