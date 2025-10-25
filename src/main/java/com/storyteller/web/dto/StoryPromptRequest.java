package com.storyteller.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StoryPromptRequest(
        @NotBlank(message = "Prompt must not be empty") String prompt,
        @Positive(message = "maxTokens must be positive")
        @Max(value = 4096, message = "maxTokens must be less than 4096")
        Integer maxTokens
) {
}
