package com.assignment.urlshortener.mapper;

import com.assignment.urlshortener.dto.UrlRequestDTO;
import com.assignment.urlshortener.dto.UrlResponseDTO;
import com.assignment.urlshortener.entity.Url;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UrlMapper {
    public Url dtoToEntityMapper(UrlRequestDTO request, String shortCode) {
        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setActive(true);
        return url;
    }

    public UrlResponseDTO entityToDtoMapper(Url savedUrl) {
        UrlResponseDTO responseDto = new UrlResponseDTO();
        responseDto.setId(savedUrl.getId());
        responseDto.setOriginalUrl(savedUrl.getOriginalUrl());
        responseDto.setShortCode(savedUrl.getShortCode());
        responseDto.setCreatedAt(savedUrl.getCreatedAt());
        return responseDto;
    }
}
