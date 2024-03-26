/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */
package eu.europa.ec.etrustex.web.rest.api;

import eu.europa.ec.etrustex.web.util.exception.ApiError;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import java.util.Objects;


@Slf4j
@ControllerAdvice(basePackages = "eu.europa.ec.etrustex.web.rest.api.messages")
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(
            @Nonnull MethodArgumentNotValidException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {

        return handleInvalidParams(ex, headers, status.value(), request);
    }

    @Override
    protected @Nonnull ResponseEntity<Object> handleBindException(
            @Nonnull BindException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {

        return handleInvalidParams(ex, headers, status.value(), request);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        String error = ex.getMessage();

        ApiError apiError = ApiError.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .type(request.getDescription(false))
                .message(error)
                .build();

        log(apiError);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getCode());
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();

        ApiError apiError = ApiError.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .type(request.getDescription(false))
                .message(error)
                .build();

        log(apiError);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getCode());
    }

    @ExceptionHandler(value = {ResponseStatusException.class })
    protected ResponseEntity<Object> handleGenericTypedException(ResponseStatusException ex, WebRequest request) {
        ApiError apiError = ApiError.builder()
                .code(ex.getStatus().value())
                .type(request.getDescription(false))
                .message(ex.getMessage())
                .build();

        log(apiError);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getCode());
    }

    @ExceptionHandler(value = {EtxWebException.class, RuntimeException.class })
    protected ResponseEntity<Object> handleGenericException(RuntimeException ex, WebRequest request) {
        String error = ex.getMessage();

        ApiError apiError = ApiError.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type(request.getDescription(false))
                .message(error)
                .build();

        log(apiError);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getCode());
    }


    private ResponseEntity<Object> handleInvalidParams(BindException ex, HttpHeaders headers, int status, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage())
                    .append("\n");
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.append(error.getObjectName()).append(": ").append(error.getDefaultMessage())
                    .append("\n");
        }

        ApiError apiError = ApiError.builder()
                .code(status)
                .type(request.getDescription(false))
                .message(errors.toString())
                .build();

        log(apiError);

        return handleExceptionInternal(ex, apiError, headers, HttpStatus.valueOf(apiError.getCode()), request);
    }

    private void log(ApiError apiError) {
        log.info(". " + apiError.getType() + apiError.getMessage());
    }
}
