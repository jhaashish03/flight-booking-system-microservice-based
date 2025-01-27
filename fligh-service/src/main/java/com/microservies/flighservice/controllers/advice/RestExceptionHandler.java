package com.microservies.flighservice.controllers.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservies.flighservice.entities.ErrorResponse;
import com.microservies.flighservice.entities.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> {
                    final FieldError fieldError = new FieldError();
                    fieldError.setErrorCode(error.getCode());
                    fieldError.setField(error.getField());
                    fieldError.setErrorMessage(error.getDefaultMessage());
                    return fieldError;
                })
                .toList();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setFieldErrors(fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = {JsonProcessingException.class})
    public ResponseEntity<ErrorResponse> handleServerSideRequestProcessingException(final Exception exception){
        return ResponseEntity.badRequest().body(ErrorResponse.builder().
                httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build());
    }


}
