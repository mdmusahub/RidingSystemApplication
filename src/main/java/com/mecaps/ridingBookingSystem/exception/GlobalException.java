package com.mecaps.ridingBookingSystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler (UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException
            (UserAlreadyExistsException excepion, HttpServletRequest request){
        ErrorResponse errorResponse =new ErrorResponse(LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                excepion.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(409));

    }

    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException
            (UserNotFoundException exception, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                exception.getMessage(), request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler (InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException
            (InvalidCredentialsException exception, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatusCode.valueOf(401));
    }

    @ExceptionHandler(RiderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRiderNotFound(RiderNotFoundException exception
                                                             ,HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), exception.getMessage(), request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }

    @ExceptionHandler(RideRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRiderNotFound(RideRequestNotFoundException exception,
                                                             HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), exception.getMessage(), request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(404));
    }
}
