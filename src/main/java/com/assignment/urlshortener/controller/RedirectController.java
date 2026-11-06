package com.assignment.urlshortener.controller;


import com.assignment.urlshortener.service.URLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/redirect")
public class RedirectController {
    @Autowired
    URLServiceImpl urlService;
    @GetMapping("/getLongUrl/{shortCode}")
    public ResponseEntity<Void> getLongUrl(@PathVariable String shortCode) {
        String urlByShortCode = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlByShortCode)).build();
    }
}
