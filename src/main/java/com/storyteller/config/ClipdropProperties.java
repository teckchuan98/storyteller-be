package com.storyteller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clipdrop")
public class ClipdropProperties {

    /**
     * Base URL for the Clipdrop API, e.g. https://clipdrop-api.co.
     */
    private String apiBaseUrl;

    /**
     * API key used to authorize requests.
     */
    private String apiKey;

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
