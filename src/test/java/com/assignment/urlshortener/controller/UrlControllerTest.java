package com.assignment.urlshortener.controller;

import com.assignment.urlshortener.dto.UrlAnalyticsResponse;
import com.assignment.urlshortener.dto.UrlClickAnalyticsDto;
import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.exception.GlobalExceptionHandler;
import com.assignment.urlshortener.service.URLServiceImpl;
import com.assignment.urlshortener.service.UrlClickServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private URLServiceImpl urlService;

    @MockitoBean
    private UrlClickServiceImpl urlClickService;

    @Test
    void getAnalytics_shouldReturnAnalyticsResponse() throws Exception {
        Url url = new Url();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://google.com");
        UrlAnalyticsResponse response = new UrlAnalyticsResponse(
                "abc123",
                "https://google.com",
                3,
                List.of(
                        new UrlClickAnalyticsDto(
                                LocalDateTime.now(),
                                "Chrome",
                                "https://facebook.com"
                        )
                )
        );

        when(urlService.getUrlByShortCodeForAnalytics("abc123"))
                .thenReturn(url);

        when(urlClickService.getAnalytics(url))
                .thenReturn(response);
        mockMvc.perform(get("/api/v1/url-shortner/abc123/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://google.com"))
                .andExpect(jsonPath("$.totalClicks").value(3))
                .andExpect(jsonPath("$.clicks").isArray())
                .andExpect(jsonPath("$.clicks.length()").value(1))
                .andExpect(jsonPath("$.clicks[0].userAgent")
                        .value("Chrome"));

        verify(urlService).getUrlByShortCodeForAnalytics("abc123");
        verify(urlClickService).getAnalytics(url);
    }
}
