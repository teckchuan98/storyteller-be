package com.storyteller.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionRequest(
        String model,
        List<Message> messages,
        double temperature,
        @JsonProperty("max_tokens") int maxTokens
) {

    public static ChatCompletionRequest forStory(String model, String prompt, int maxTokens) {
        List<Message> chatMessages = List.of(
                new Message("system", "You craft vivid, interactive stories. Always open with concrete sensory detail about the protagonist's physical appearance (face structure, complexion, freckles, scars, clothing textures) before advancing the plot, then invite the reader to choose what happens next."),
                new Message("user", prompt)
        );
        return new ChatCompletionRequest(model, chatMessages, 0.8, maxTokens);
    }

    public record Message(String role, String content) {
    }
}
