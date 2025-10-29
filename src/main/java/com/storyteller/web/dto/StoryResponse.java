package com.storyteller.web.dto;

public record StoryResponse(
        String story,
        String imageBase64,
        String imageMimeType
) {
}
