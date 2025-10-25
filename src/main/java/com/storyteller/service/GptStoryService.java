package com.storyteller.service;

import com.storyteller.config.OpenAiProperties;
import com.storyteller.openai.dto.ChatCompletionRequest;
import com.storyteller.openai.dto.ChatCompletionResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

@Service
public class GptStoryService {

    private final RestTemplate restTemplate;
    private final OpenAiProperties properties;

    public GptStoryService(RestTemplateBuilder restTemplateBuilder, OpenAiProperties properties) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.getApiBaseUrl())
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }

    private static final int DEFAULT_MAX_TOKENS = 400;

    public String generateStoryContinuation(String prompt, Integer requestedMaxTokens) {
        Assert.hasText(properties.getApiKey(), "OpenAI API key is missing. Set openai.api-key or OPENAI_API_KEY.");

        int maxTokens = Optional.ofNullable(requestedMaxTokens)
                .filter(value -> value > 0)
                .orElse(DEFAULT_MAX_TOKENS);

        ChatCompletionRequest payload = ChatCompletionRequest.forStory(properties.getModel(), prompt, maxTokens);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(payload, headers);

        ChatCompletionResponse response = restTemplate.postForObject(
                "/chat/completions",
                entity,
                ChatCompletionResponse.class
        );

        return Optional.ofNullable(response)
                .flatMap(resp -> resp.choices().stream().findFirst())
                .map(ChatCompletionResponse.Choice::message)
                .map(ChatCompletionResponse.Message::content)
                .orElseThrow(() -> new IllegalStateException("No story returned by GPT-4o-mini."));
    }
}
