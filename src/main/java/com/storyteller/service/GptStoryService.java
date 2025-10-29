package com.storyteller.service;

import com.storyteller.config.OpenAiProperties;
import com.storyteller.openai.dto.ChatCompletionRequest;
import com.storyteller.openai.dto.ChatCompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(GptStoryService.class);
    private final RestTemplate restTemplate;
    private final OpenAiProperties properties;

    public GptStoryService(RestTemplateBuilder restTemplateBuilder, OpenAiProperties properties) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.getApiBaseUrl())
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(180))  // Increased to 3 minutes for full story generation
                .build();
    }

    private static final int DEFAULT_MAX_TOKENS = 400;
    private static final int INTRO_MAX_TOKENS = 7000;  // Increased to accommodate 3,000-3,500 words (including Malay support)

    public String generateStoryContinuation(String prompt, Integer requestedMaxTokens, String requestType) {
        Assert.hasText(properties.getApiKey(), "OpenAI API key is missing. Set openai.api-key or OPENAI_API_KEY.");

        // For intro requests (full story generation), use higher token limit
        int defaultTokens = "intro".equals(requestType) ? INTRO_MAX_TOKENS : DEFAULT_MAX_TOKENS;

        int maxTokens = Optional.ofNullable(requestedMaxTokens)
                .filter(value -> value > 0)
                .orElse(defaultTokens);

        ChatCompletionRequest payload = ChatCompletionRequest.forStory(properties.getModel(), prompt, maxTokens, requestType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(payload, headers);

        ChatCompletionResponse response = restTemplate.postForObject(
                "/chat/completions",
                entity,
                ChatCompletionResponse.class
        );

        String story = Optional.ofNullable(response)
                .flatMap(resp -> resp.choices().stream().findFirst())
                .map(ChatCompletionResponse.Choice::message)
                .map(ChatCompletionResponse.Message::content)
                .orElseThrow(() -> new IllegalStateException("No story returned by GPT-4o-mini."));

        LOG.info("OpenAI story response:\n{}", story);
        return story;
    }
}
