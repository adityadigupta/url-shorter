package com.assignment.urlshortener.service;

import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.exception.NoUrlException;
import com.assignment.urlshortener.mapper.UrlMapper;
import com.assignment.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;
    @InjectMocks
    URLServiceImpl urlService;
    @Spy
    UrlMapper urlMapper;

    @Test
    void shouldReturnUrlIfShortCodeExists() {

        Url url = new Url();
        url.setOriginalUrl("https://google.com");
        url.setShortCode("abc");
        url.setActive(Boolean.TRUE);
        url.setCreatedAt(LocalDateTime.now());
        url.setId(1L);
        when(urlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));
        String result = urlService.getUrlByShortCode("abc");
        assertEquals("https://google.com", result);

    }

    @Test
    void shouldExceptionWhenNoShortCode() {
        when(urlRepository.findByShortCode("abc")).thenReturn(Optional.empty());
        assertThrows(NoUrlException.class, () -> urlService.getUrlByShortCode("abc"));
    }
    @Test
    void shouldExceptionWhenShortCodeInactive() {
        Url url = new Url();
        url.setOriginalUrl("https://google.com");
        url.setShortCode("abc");
        url.setActive(Boolean.FALSE);
        url.setCreatedAt(LocalDateTime.now());
        url.setId(1L);
        when(urlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));
        assertThrows(NoUrlException.class, () -> urlService.getUrlByShortCode("abc"));
    }


}
