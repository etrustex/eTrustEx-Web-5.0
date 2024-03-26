package eu.europa.ec.etrustex.web.rest.config;

import eu.europa.ec.etrustex.web.exchange.model.ServerValidationErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String REDIRECTION = "/goto/";

    @ExceptionHandler(value = { AccessDeniedException.class })
    protected ResponseEntity<Object> handleConflict(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied for {} {}", request.toString(), ex.toString());
        return handleExceptionInternal(ex, "Access Denied!", new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        log.error(request.toString());
        log.error(ex.toString(), ex);
        String body = "Error!";

        if(((ServletWebRequest) request).getRequest().getRequestURI().contains(REDIRECTION)){
            body = "The link you're trying to access does not exist anymore.";
        }

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.warn("Invalid request for {} {}", request.toString(), ex.toString());

        return new ResponseEntity<>(buildServerValidationErrors(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        log.error("Error for {} {}", request.toString(), ex.toString());
        return handleExceptionInternal(ex, "Error!", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn("Invalid request for {} {}", request.toString(), ex.toString());

        return new ResponseEntity<>(buildServerValidationErrors(ex), headers, status);
    }

    private ServerValidationErrors buildServerValidationErrors(Exception e) {
        List<String> errors;

        if (e instanceof MethodArgumentNotValidException) {
            errors = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        } else if (e instanceof ConstraintViolationException) {
            errors = ((ConstraintViolationException) e).getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
        } else {
            errors = Collections.singletonList("Unknown validation error. " + e.getMessage());
        }

        return ServerValidationErrors.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .build();
    }
}
