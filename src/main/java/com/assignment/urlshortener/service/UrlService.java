package com.assignment.urlshortener.service;

import com.assignment.urlshortener.dto.UrlRequestDTO;
import com.assignment.urlshortener.dto.UrlResponseDTO;
import com.assignment.urlshortener.entity.Url;

public interface UrlService {
     UrlResponseDTO createShortUrl(UrlRequestDTO request);
    Url getUrlByShortCodeClick(String shortCode);
     Url getUrlByShortCodeForAnalytics(String shortCode);
}
