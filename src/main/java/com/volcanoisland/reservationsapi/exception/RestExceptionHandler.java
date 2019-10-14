package com.volcanoisland.reservationsapi.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles NotFoundException, thrown when a nonexistent resource is requested.
     *
     * @param ex A NotFoundException instance.
     * @return A ResponseEntity with HTTP status code 404.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        logger.info("NotFoundException: " + ex.getMessage());
        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND, ex.getMessage());
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), errorDto.getStatus(), request);
    }

    /**
     * Handles BadRequestException.
     *
     * @param ex A BadRequestException instance.
     * @return A ResponseEntity with HTTP status code 400.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logger.info("BadRequestException: " + ex.getMessage());
        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, ex.getMessage());
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), errorDto.getStatus(), request);
    }

    /**
     * Handles MethodNotAllowedException, thrown when attempting to updated a cancelled reservation.
     *
     * @param ex A MethodNotAllowedException instance.
     * @return A ResponseEntity with HTTP status code 405.
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<Object> handleMethodNotAllowedException(MethodNotAllowedException ex, WebRequest request) {
        logger.info("MethodNotAllowedException: " + ex.getMessage());
        ErrorDto errorDto = new ErrorDto(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), errorDto.getStatus(), request);
    }

    /**
     * Handles UnavailableDatesException, thrown when an attempt to reserve already taken days is made.
     *
     * @param ex A UnavailableDatesException instance.
     * @return A ResponseEntity with HTTP status code 409 (Conflict).
     */
    @ExceptionHandler(UnavailableDatesException.class)
    public ResponseEntity<Object> handleUnavailableDatesException(UnavailableDatesException ex, WebRequest request) {
        logger.info("UnavailableDatesException: " + ex.getMessage());
        ErrorDto errorDto = new ErrorDto(HttpStatus.CONFLICT, "Reservation failed", ex.getMessage());
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), errorDto.getStatus(), request);
    }


    /**
     * Handles ConstraintViolationException.
     *
     * @param ex A ConstraintViolationException instance.
     * @return A ResponseEntity with HTTP status code 400.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, ex.getMessage(), errors);
        return new ResponseEntity<>(errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    //
    // Override default handlers to provide a unified error response:
    //

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, "Parameters validation failed", errors);
        return handleExceptionInternal(ex, errorDto, headers, errorDto.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {

        String error = ex.getParameterName() + " parameter is missing";
        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, ex.getMessage(), error);
        return new ResponseEntity<>(errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {

        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatus status,
                                                                         WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");

        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        ErrorDto errorDto = new ErrorDto(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<>(errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatus status,
                                                                     WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

        ErrorDto errorDto = new ErrorDto(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));

        return new ResponseEntity<>(errorDto, new HttpHeaders(), errorDto.getStatus());
    }

    /**
     * Handles all Exceptions not addressed by more specific @ExceptionHandler methods.
     *
     * @param ex An Exception instance.
     * @return A ResponseEntity with HTTP status code 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unhandled Exception: ", ex);
        ErrorDto errorDto = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "An error occurred");
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), errorDto.getStatus(), request);
    }

}
