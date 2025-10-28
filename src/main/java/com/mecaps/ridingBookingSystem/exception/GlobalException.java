package com.mecaps.ridingBookingSystem.exception;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler (UserAlreadyExistsExseption.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException
            (UserAlreadyExistsExseption excepion, HttpServletRequest request){
        ErrorResponse errorResponse =new ErrorResponse(LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                excepion.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(409));

    }

    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound
            (UserNotFoundException exception, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                exception.getMessage(), request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }

}
