package com.storyteller.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StoryPromptRequest(
        @NotBlank(message = "Prompt must not be empty") String prompt,
        @Positive(message = "maxTokens must be positive")
        @Max(value = 10000, message = "maxTokens must be less than 10000")
        Integer maxTokens,
        Boolean generateImage,
        String requestType
) {

    public boolean shouldGenerateImage() {
        return Boolean.TRUE.equals(generateImage);
    }

    public String getRequestType() {
        return requestType != null ? requestType : "continuation";
    }
}
