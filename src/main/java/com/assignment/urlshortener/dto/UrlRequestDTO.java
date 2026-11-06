package com.assignment.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRequestDTO {

    @NotBlank(message = "Original URL cannot be empty")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String originalUrl;
}
