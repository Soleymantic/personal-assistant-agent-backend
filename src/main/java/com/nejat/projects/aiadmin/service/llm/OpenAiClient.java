package com.nejat.projects.aiadmin.service.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.nejat.projects.aiadmin.config.OpenAiProperties;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenAiClient {

    private final OpenAiProperties openAiProperties;
    private final WebClient webClient;

    public OpenAiClient(OpenAiProperties openAiProperties, @Qualifier("openAiWebClient") WebClient webClient) {
        this.openAiProperties = openAiProperties;
        this.webClient = webClient;
    }

    public String chat(List<Map<String, String>> messages) {
        JsonNode response = webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", openAiProperties.getModel(),
                        "messages", messages))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null
                || !response.has("choices")
                || !response.get("choices").isArray()
                || response.get("choices").isEmpty()) {
            throw new RuntimeException("Invalid response from OpenAI chat completions endpoint.");
        }

        JsonNode contentNode = response.path("choices").get(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.isNull() || contentNode.asText(null) == null) {
            throw new RuntimeException("Missing message content in OpenAI response.");
        }

        return contentNode.asText();
    }
}
