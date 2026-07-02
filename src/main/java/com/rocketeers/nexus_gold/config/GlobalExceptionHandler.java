package com.rocketeers.nexus_gold.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Unsupported media type");
        response.put("message", "The request content type is not supported. Please ensure form data is properly formatted.");
        response.put("supportedTypes", "application/json, application/octet-stream, multipart/form-data");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }
}
