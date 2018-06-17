package com.upday.news.controller.errors;

import com.upday.news.controller.errors.dto.ErrorDto;
import com.upday.news.controller.errors.dto.ParameterizedErrorDto;
import com.upday.news.controller.errors.exceptions.CustomParameterizedException;
import org.hibernate.PersistentObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Controller advice to convert the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDto processConcurrencyError(ConcurrencyFailureException ex) {
        return new ErrorDto(ErrorConstants.ERR_CONCURRENCY_FAILURE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ErrorDto dto = new ErrorDto(ErrorConstants.ERR_VALIDATION);
        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }
        return dto;
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorDto processParameterizedValidationError(CustomParameterizedException ex) {
        return ex.getErrorDto();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDto processContraintViolationError(ConstraintViolationException ex) {
        return new ErrorDto(ErrorConstants.ERR_CONSTRAINT_VIOLATION, ex.getMessage());
    }

    @ExceptionHandler(PersistentObjectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto processPersistentObjectException(PersistentObjectException ex) {
        return new ErrorDto(ErrorConstants.ERR_PERSISTENT_OBJECT_EXCEPTION, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> processException(Exception ex) {
        if(log.isDebugEnabled()){
            log.debug("An unexpected error occurred: {}", ex.getMessage(), ex);
        } else {
            log.error("An unexpected error occurred: {}", ex.getMessage());
        }
        ResponseEntity.BodyBuilder builder;
        ErrorDto errorDto;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if(responseStatus != null){
            builder = ResponseEntity.status(responseStatus.value());
            errorDto = new ErrorDto("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            errorDto = new ErrorDto(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
        }
        return builder.body(errorDto);
    }
}
