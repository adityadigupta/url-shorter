package com.assignment.urlshortener.service;

import com.assignment.urlshortener.dto.UrlRequestDTO;
import com.assignment.urlshortener.dto.UrlResponseDTO;
import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.exception.NoUrlException;
import com.assignment.urlshortener.mapper.UrlMapper;
import com.assignment.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class URLServiceImpl implements UrlService {
    @Autowired
    UrlRepository urlRepository;
    @Autowired
    UrlMapper urlMapper;
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public UrlResponseDTO createShortUrl(UrlRequestDTO request) {
        String shortCode = generateUniqueShortCode();
        Url savedUrl = urlRepository.save(urlMapper.dtoToEntityMapper(request, shortCode));
        UrlResponseDTO responseDto = urlMapper.entityToDtoMapper(savedUrl);
        responseDto.setShortUrl(baseUrl + "/" + savedUrl.getShortCode());
        return responseDto;
    }


    private String generateUniqueShortCode() {
        String shortCode;

        do {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                code.append(CHARACTERS.charAt(index));
            }
            shortCode = code.toString();

        } while (urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    public String getUrlByShortCode(String shortCode) {
        return getUrlByShortCodeClick(shortCode).getOriginalUrl();
    }

    @Override
    public Url getUrlByShortCodeClick(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new NoUrlException(
                                "No url mapped to short code " + shortCode
                        ));

        if (!url.isActive()) {
            throw new NoUrlException(shortCode + " is not active.");
        }

        if (url.getExpiresAt() != null
                && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new NoUrlException(shortCode + " is expired.");
        }
        return url;
    }
    @Override
    public Url getUrlByShortCodeForAnalytics(String shortCode) {

        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new NoUrlException(
                                "No url mapped to short code " + shortCode
                        ));
    }

}
