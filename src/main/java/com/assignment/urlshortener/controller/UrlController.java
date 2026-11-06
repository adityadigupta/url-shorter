package com.assignment.urlshortener.controller;

import com.assignment.urlshortener.dto.UrlAnalyticsResponse;
import com.assignment.urlshortener.dto.UrlRequestDTO;
import com.assignment.urlshortener.dto.UrlResponseDTO;
import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.service.URLServiceImpl;
import com.assignment.urlshortener.service.UrlClickServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/url-shortner")
public class UrlController {
    @Autowired
    URLServiceImpl urlService;

    @Autowired
    UrlClickServiceImpl urlClickService;

    @PostMapping("/addUrl")
    public ResponseEntity<UrlResponseDTO> createShortUrl(@Valid @RequestBody UrlRequestDTO urlRequestDTO) {
        UrlResponseDTO responseDTO = urlService.createShortUrl(urlRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{shortCode}/analytics")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(
            @PathVariable String shortCode) {
        Url url = urlService.getUrlByShortCodeForAnalytics(shortCode);
        UrlAnalyticsResponse response = urlClickService.getAnalytics(url);
        return ResponseEntity.ok(response);
    }
}
