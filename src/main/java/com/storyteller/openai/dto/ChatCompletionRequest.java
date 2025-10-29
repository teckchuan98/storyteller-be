package com.storyteller.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionRequest(
        String model,
        List<Message> messages,
        double temperature,
        @JsonProperty("max_tokens") int maxTokens
) {

    public static ChatCompletionRequest forStory(String model, String prompt, int maxTokens, String requestType) {
        String systemMessage = getSystemMessageForRequestType(requestType);
        List<Message> chatMessages = List.of(
                new Message("system", systemMessage),
                new Message("user", prompt)
        );
        return new ChatCompletionRequest(model, chatMessages, 0.7, maxTokens);
    }

    private static String getSystemMessageForRequestType(String requestType) {
        if ("intro".equals(requestType)) {
            return "You are a literary novelist. Generate a COMPLETE, SELF-CONTAINED story of 3,000-3,500 words. " +
                   "You MUST write the full story arc from beginning to end in this single response. " +
                   "Use rich sensory details, poetic realism, and follow the provided emotional structure completely. " +
                   "The story must reach its natural conclusion - do not stop early or leave the narrative incomplete.";
        } else {
            // For "continuation" or any other type
            return "You are a creative AI assistant continuing an established interactive story. " +
                   "Focus on plot progression and emotional development. Only mention environment or character details " +
                   "when they change or become newly relevant. Maintain dynamic pacing.";
        }
    }

    public record Message(String role, String content) {
    }
}
