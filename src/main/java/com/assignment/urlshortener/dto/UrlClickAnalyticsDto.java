package com.assignment.urlshortener.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class UrlClickAnalyticsDto {
    private LocalDateTime accessedAt;
    private String userAgent;
    private String referrer;

    public UrlClickAnalyticsDto(LocalDateTime accessedAt, String userAgent, String referrer) {
        this.accessedAt=accessedAt;
        this.referrer=referrer;
        this.userAgent=userAgent;
    }
    UrlClickAnalyticsDto (){

    }
}
