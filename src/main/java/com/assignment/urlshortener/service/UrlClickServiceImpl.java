package com.assignment.urlshortener.service;

import ch.qos.logback.core.spi.ErrorCodes;
import com.assignment.urlshortener.constants.ErrorCode;
import com.assignment.urlshortener.dto.UrlAnalyticsResponse;
import com.assignment.urlshortener.dto.UrlClickAnalyticsDto;
import com.assignment.urlshortener.entity.Url;
import com.assignment.urlshortener.entity.UrlClick;
import com.assignment.urlshortener.exception.NoUrlException;
import com.assignment.urlshortener.repository.UrlClickRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlClickServiceImpl implements UrlClickService{

    @Autowired
    UrlClickRepository urlClickRepository;

    @Transactional
    public void recordClick(Url url, String userAgent, String referrer) {

        UrlClick click = new UrlClick();
        click.setUrl(url);
        click.setAccessedAt(LocalDateTime.now());
        click.setUserAgent(userAgent);
        click.setReferrer(referrer);
        urlClickRepository.save(click);
    }
    @Override
    public UrlAnalyticsResponse getAnalytics(Url url) {

        List<UrlClick> clickRecords =
                urlClickRepository.findByUrlIdOrderByAccessedAtDesc(
                        url.getId()
                );

        List<UrlClickAnalyticsDto> clicks = clickRecords.stream()
                .map(click -> new UrlClickAnalyticsDto(
                        click.getAccessedAt(),
                        click.getUserAgent(),
                        click.getReferrer()
                ))
                .toList();

        return new UrlAnalyticsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                clicks.size(),
                clicks
        );
    }

}
