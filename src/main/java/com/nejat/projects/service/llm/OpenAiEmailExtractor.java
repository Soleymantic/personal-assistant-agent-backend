package com.nejat.projects.service.llm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nejat.projects.aiadmin.service.llm.OpenAiClient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("openai")
public class OpenAiEmailExtractor implements EmailExtractor {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public OpenAiEmailExtractor(OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> extract(String emailText) {
        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role",
                        "system",
                        "content",
                        "You are an information extraction engine for German administrative emails.\n"
                                + "Return one valid JSON object with EXACT fields:\n"
                                + "- title (string)\n"
                                + "- sender (string)\n"
                                + "- amount (string)\n"
                                + "- due_date (string or null, ISO format YYYY-MM-DD)\n"
                                + "- summary (string, short German summary)\n"
                                + "- tags (array of short lowercase strings)\n"
                                + "Do NOT add text outside JSON."),
                Map.of(
                        "role", "user", "content", String.format("Extract fields from the following email:%n<<<%n%s%n>>>", emailText))
        );

        String response = openAiClient.chat(messages);
        if (response == null || response.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }
}
