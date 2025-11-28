package com.nejat.projects.service.llm;

import com.nejat.projects.aiadmin.service.llm.OpenAiClient;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("openai")
public class OpenAiEmailClassifier implements EmailClassifier {

    private final OpenAiClient openAiClient;

    public OpenAiEmailClassifier(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public EmailCategory classify(String emailText) {
        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role", "system",
                        "content", "You are a strict email classifier. You must output one of:\n"
                                + "INVOICE, CONTRACT, OFFICIAL_LETTER, SPAM, OTHER.\n"
                                + "Output only the uppercase label. No extra text."),
                Map.of(
                        "role", "user",
                        "content", String.format("Classify the following email:%n<<<%n%s%n>>>", emailText))
        );

        String response = openAiClient.chat(messages);
        String trimmed = response != null ? response.trim() : "";

        try {
            return EmailCategory.valueOf(trimmed);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return EmailCategory.OTHER;
        }
    }
}
