package eu.europa.ec.etrustex.web.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import java.util.Collection;

@Configuration
@Aspect
@Slf4j
@SuppressWarnings({"squid:S1186"})
public class ServicePerformanceAspect {
    @Around("@within(org.springframework.stereotype.Service)")
    public Object profileAllMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
    {
        Object result;
        if(showLog()){
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

            String className = methodSignature.getDeclaringType().getSimpleName();
            String methodName = methodSignature.getName();

            final StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            result = proceedingJoinPoint.proceed();
            stopWatch.stop();
            log.debug(className + "." + methodName + " :: " + stopWatch.getTotalTimeMillis() + " ms");
            displayArguments(proceedingJoinPoint, methodSignature);
        }else{
            result = proceedingJoinPoint.proceed();
        }

        return result;
    }

    private void displayArguments(ProceedingJoinPoint proceedingJoinPoint, MethodSignature signature){
        int params = signature.getParameterNames().length;
        String paramName;
        Class paramType;
        Object paramValue;
        for (int i=0; i<params; i++) {
            paramName = signature.getParameterNames()[i];
            paramType = signature.getParameterTypes()[i];
            paramValue = proceedingJoinPoint.getArgs()[i] != null ? proceedingJoinPoint.getArgs()[i] : "";
            log.debug(String.format("Param #%s: Name: %s, Type: %s, Value: %s",i, paramName, paramType.getSimpleName(), paramValue.toString()));
        }
    }
    private boolean showLog(){
        return log.isDebugEnabled();
    }
}
