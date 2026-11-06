package com.assignment.urlshortener.exception;

public class NoUrlException extends RuntimeException {
    String message;

    public NoUrlException(String message) {
        super((message));
    }
}
