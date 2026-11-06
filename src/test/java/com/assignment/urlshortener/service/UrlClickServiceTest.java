package com.assignment.urlshortener.service;

import com.assignment.urlshortener.dto.UrlAnalyticsResponse;
import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.entity.UrlClick;
import com.assignment.urlshortener.repository.UrlClickRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlClickServiceTest {
    @Mock
    private UrlClickRepository urlClickRepository;

    @InjectMocks
    private UrlClickServiceImpl urlClickService;

    @Test
    void recordClick_shouldSaveClickDetails() {

        Url url = new Url();
        url.setId(1L);
        url.setShortCode("abc123");
        String userAgent = "Chrome Browser";
        String referrer = "https://google.com";
        urlClickService.recordClick(url, userAgent, referrer);
        verify(urlClickRepository, times(1))
                .save(any(UrlClick.class));
    }

    @Test
    void getAnalytics_shouldReturnClickDetails() {
        Url url = new Url();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://google.com");
        List<UrlClick> clickRecords = new ArrayList<>();
        UrlClick UrlClick1 = new UrlClick();
        UrlClick1.setUrl(url);
        UrlClick1.setAccessedAt(LocalDateTime.now());
        UrlClick1.setUserAgent("Chrome");
        UrlClick1.setReferrer("https://facebook.com");
        UrlClick UrlClick2 = new UrlClick();
        UrlClick2.setUrl(url);
        UrlClick2.setAccessedAt(LocalDateTime.now());
        UrlClick2.setUserAgent("Firefox");
        UrlClick2.setReferrer("https://google.com");
        UrlClick UrlClick3 = new UrlClick();
        UrlClick3.setUrl(url);
        UrlClick3.setAccessedAt(LocalDateTime.now());
        UrlClick3.setUserAgent("Safari");
        UrlClick3.setReferrer(null);
        clickRecords.add(UrlClick1);
        clickRecords.add(UrlClick2);
        clickRecords.add(UrlClick3);
        when(urlClickRepository.findByUrlIdOrderByAccessedAtDesc(1L))
                .thenReturn(clickRecords);
        UrlAnalyticsResponse response =
                urlClickService.getAnalytics(url);
        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());
        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals(3, response.getTotalClicks());
        assertEquals(3, response.getClicks().size());
        assertEquals("Chrome",
                response.getClicks().get(0).getUserAgent());
        verify(urlClickRepository, times(1))
                .findByUrlIdOrderByAccessedAtDesc(1L);
    }

    @Test
    void getAnalytics_shouldReturnEmptyListWhenNoClicksExist() {
        Url url = new Url();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://google.com");
        when(urlClickRepository.findByUrlIdOrderByAccessedAtDesc(1L))
                .thenReturn(List.of());
        UrlAnalyticsResponse response =
                urlClickService.getAnalytics(url);
        assertNotNull(response);
        assertEquals(0, response.getTotalClicks());
        assertTrue(response.getClicks().isEmpty());
        verify(urlClickRepository, times(1))
                .findByUrlIdOrderByAccessedAtDesc(1L);
    }

}
