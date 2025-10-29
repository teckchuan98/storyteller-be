package com.storyteller.service;

import com.storyteller.config.ClipdropProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
public class ClipdropImageService {

    private static final MediaType PNG_MEDIA_TYPE = MediaType.IMAGE_PNG;
    private static final int PROMPT_CHAR_LIMIT = 1000;

    private final ClipdropProperties properties;
    private final RestTemplate restTemplate;

    public ClipdropImageService(RestTemplateBuilder restTemplateBuilder, ClipdropProperties properties) {
        this.properties = properties;
        Assert.hasText(properties.getApiBaseUrl(), "Clipdrop API base URL is missing. Set clipdrop.api-base-url or CLIPDROP_API_BASE_URL.");
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.getApiBaseUrl())
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(90))
                .build();
    }

    /**
     * Generates a base64-encoded PNG image from the supplied prompt text using the Clipdrop API.
     *
     * @param prompt the prompt text that describes the desired image
     * @return a {@link GeneratedImage} holding the encoded image and MIME type
     */
    public GeneratedImage generateImage(String prompt) {
        Assert.hasText(properties.getApiKey(), "Clipdrop API key is missing. Set clipdrop.api-key or CLIPDROP_API_KEY.");
        Assert.hasText(properties.getApiBaseUrl(), "Clipdrop API base URL is missing. Set clipdrop.api-base-url or CLIPDROP_API_BASE_URL.");
        Assert.hasText(prompt, "Prompt for image generation must not be blank.");

        String resolvedPrompt = sanitizePrompt(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("x-api-key", properties.getApiKey());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt", resolvedPrompt);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity("/text-to-image/v1", request, byte[].class);

        MediaType responseType = response.getHeaders().getContentType();
        byte[] responseBody = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && responseBody != null && responseType != null && responseType.includes(PNG_MEDIA_TYPE)) {
            String encoded = Base64.getEncoder().encodeToString(responseBody);
            return new GeneratedImage(encoded, PNG_MEDIA_TYPE.toString());
        }

        if (responseBody != null && responseBody.length > 0 && responseType != null && MediaType.APPLICATION_JSON.includes(responseType)) {
            String errorMessage = new String(responseBody, StandardCharsets.UTF_8);
            throw new IllegalStateException("Clipdrop image generation failed: " + errorMessage);
        }

        throw new IllegalStateException("Clipdrop image generation failed with status %s".formatted(response.getStatusCode()));
    }

    private String sanitizePrompt(String prompt) {
        String trimmed = prompt.trim();
        if (trimmed.length() <= PROMPT_CHAR_LIMIT) {
            return trimmed;
        }
        return trimmed.substring(0, PROMPT_CHAR_LIMIT);
    }

    public record GeneratedImage(String base64Data, String mimeType) {
    }
}
