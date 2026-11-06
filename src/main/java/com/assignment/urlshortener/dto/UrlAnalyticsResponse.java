package com.assignment.urlshortener.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UrlAnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private long totalClicks;
    private List<UrlClickAnalyticsDto> clicks;

    public UrlAnalyticsResponse(String shortCode, String originalUrl, long totalClicks, List<UrlClickAnalyticsDto> clicks) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.totalClicks = totalClicks;
        this.clicks = clicks;
    }
}
