package com.storyteller.web;

import com.storyteller.service.ClipdropImageService;
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
    private final ClipdropImageService clipdropImageService;

    public StoryController(GptStoryService gptStoryService, ClipdropImageService clipdropImageService) {
        this.gptStoryService = gptStoryService;
        this.clipdropImageService = clipdropImageService;
    }

    @GetMapping(path = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public String health() {
        return "storyteller-ok";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public StoryResponse generate(@Valid @RequestBody StoryPromptRequest request) {
        String story = gptStoryService.generateStoryContinuation(request.prompt(), request.maxTokens(), request.getRequestType());

        if (request.shouldGenerateImage()) {
            // Temporarily disabled: image generation is paused.
            return new StoryResponse(story, null, null);
        }

        return new StoryResponse(story, null, null);
    }
}
