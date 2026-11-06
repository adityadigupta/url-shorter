package com.assignment.urlshortener.exception;

import com.assignment.urlshortener.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoUrlException.class)
    public ResponseEntity<Map<String, String>> handleNoUrlException(NoUrlException noUrlException) {
        Map<String, String> map = new HashMap<>();
        map.put("error", ErrorCode.URL_NOT_FOUND.getCode());
        map.put("error_message", noUrlException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleNoUrlException(IllegalStateException noUrlException) {
        Map<String, String> map = new HashMap<>();
        map.put("error", ErrorCode.INVALID_DESTINATION.getCode());
        map.put("error_message", noUrlException.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
    }
}
