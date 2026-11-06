package com.assignment.urlshortener.service;

import com.assignment.urlshortener.dto.UrlAnalyticsResponse;
import com.assignment.urlshortener.entity.Url;

public interface UrlClickService {
    public UrlAnalyticsResponse getAnalytics(Url url);
    public void recordClick(Url url, String userAgent, String referrer);
}
