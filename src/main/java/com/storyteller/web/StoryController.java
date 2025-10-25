package com.storyteller.web;

import com.storyteller.service.GptStoryService;
import com.storyteller.web.dto.StoryPromptRequest;
import com.storyteller.web.dto.StoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/story", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoryController {

    private final GptStoryService gptStoryService;

    public StoryController(GptStoryService gptStoryService) {
        this.gptStoryService = gptStoryService;
    }

    @GetMapping(path = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String health() {
        return "storyteller-ok";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public StoryResponse generate(@Valid @RequestBody StoryPromptRequest request) {
        String story = gptStoryService.generateStoryContinuation(request.prompt(), request.maxTokens());
        return new StoryResponse(story);
    }
}
