package com.reactive.application.goreactive.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//it will handle all the controller exceptions, its a global exception handler @ global level
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerRunTimeException(RuntimeException ex){
        log.error("Exception caught in handleRuntimeException: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerAnyException(Exception ex){
        log.error("Exception caught in handleRuntimeException: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

}
