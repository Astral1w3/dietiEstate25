package com.dietiestates2025.dieti.exception;

import com.dietiestates2025.dieti.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponse> handleFileStorageException(FileStorageException ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

     @ExceptionHandler({JsonProcessingException.class, IOException.class})
    public ResponseEntity<ApiResponse> handleJsonParsingException(Exception ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, "Errore nei dati inviati: il formato JSON non è valido.");
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(false, "Si è verificato un errore interno. Riprova più tardi.");
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}