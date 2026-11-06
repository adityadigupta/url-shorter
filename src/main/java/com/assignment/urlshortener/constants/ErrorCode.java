package com.assignment.urlshortener.constants;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    URL_NOT_FOUND(
            "URL_NOT_FOUND",
            HttpStatus.NOT_FOUND
    ),

    URL_INACTIVE(
            "URL_INACTIVE",
            HttpStatus.NOT_FOUND
    ),

    URL_EXPIRED(
            "URL_EXPIRED",
            HttpStatus.GONE
    ),

    INVALID_DESTINATION(
            "INVALID_DESTINATION",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INVALID_REQUEST(
            "INVALID_REQUEST",
            HttpStatus.BAD_REQUEST
    );

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code,  HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }



    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
