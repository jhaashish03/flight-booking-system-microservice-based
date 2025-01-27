package com.microservies.flightservice.controllers.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservies.flightservice.entities.ErrorResponse;
import com.microservies.flightservice.entities.FieldError;
import com.microservies.flightservice.exceptions.FlightAlreadyExitsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
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
    public ResponseEntity<ErrorResponse> handleServerSideRequestProcessingException(final JsonProcessingException exception){
        return new ResponseEntity<>(ErrorResponse.builder().
                httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = {FlightAlreadyExitsException.class})
    public ResponseEntity<ErrorResponse> handleFlightAlreadyExits(final FlightAlreadyExitsException exception){
        return new ResponseEntity<>(ErrorResponse.builder().
                httpStatus(HttpStatus.CONFLICT.value())
                .exception(exception.getClass().getSimpleName())
                .message(exception.getMessage())
                .build(),HttpStatus.CONFLICT);
    }


}
