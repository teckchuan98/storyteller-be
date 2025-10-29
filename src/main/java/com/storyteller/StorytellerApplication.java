package com.storyteller;

import com.storyteller.config.ClipdropProperties;
import com.storyteller.config.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OpenAiProperties.class, ClipdropProperties.class})
public class StorytellerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorytellerApplication.class, args);
    }
}
