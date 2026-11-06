package com.assignment.urlshortener.controller;

import com.assignment.urlshortener.constants.ErrorCode;
import com.assignment.urlshortener.exception.GlobalExceptionHandler;
import com.assignment.urlshortener.exception.NoUrlException;
import com.assignment.urlshortener.service.URLServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
@Import(GlobalExceptionHandler.class)
public class RedirectTestController {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    URLServiceImpl urlService;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        when(urlService.getUrlByShortCode("abc")).thenReturn("https://google.com");
        mockMvc.perform(get("/redirect/getLongUrl/abc")).andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));
    }
    @Test
    void redirectUrl404() throws Exception {
        when(urlService.getUrlByShortCode("abc1")).thenThrow(new NoUrlException("No url mapped to short code abc1 ."));
        mockMvc.perform(get("/invalid"))
                .andExpect(status().isNotFound());

    }


}
