package eu.europa.ec.etrustex.web.service.validation.post_auth_validation;

import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Performs validation based on group {@link PostAuthorization} on methods annotated as {@link PostAuthValidated}
 *
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@RequiredArgsConstructor
public class PostAuthValidationAspect {

    private final Validator validator;

    @Before("@annotation(eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated)")
    public void validatedOnPostAuthorization(JoinPoint joinPoint) {

        if(joinPoint instanceof MethodInvocationProceedingJoinPoint) {

            Class<?>[] groups = initGroups(joinPoint);

            MethodInvocationProceedingJoinPoint methodInvocation = (MethodInvocationProceedingJoinPoint)joinPoint;

            Set<ConstraintViolation<Object>> result = Arrays.stream(methodInvocation.getArgs()).map(arg -> validator.validate(arg, groups))
                    .flatMap(Collection::stream).collect(Collectors.toSet());

            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result);
            }
        }
    }

    private Class<?> [] initGroups(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        PostAuthValidated annotation = method.getAnnotation(PostAuthValidated.class);

        return annotation.groups();
    }

}
