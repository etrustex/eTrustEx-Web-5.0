package eu.europa.ec.etrustex.web.service.validation.post_auth_validation;

import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostAuthValidated {
    Class<?> [] groups() default { PostAuthorization.class };
}
