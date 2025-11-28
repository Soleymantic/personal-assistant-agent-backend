package com.nejat.projects.aiadmin.service.llm;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class FakeEmailClassifier implements EmailClassifier {

    @Override
    public String classify(String emailText) {
        if (emailText == null || emailText.isBlank()) {
            return "uncategorized";
        }
        String lowerText = emailText.toLowerCase();
        if (lowerText.contains("invoice") || lowerText.contains("bill")) {
            return "invoice";
        }
        if (lowerText.contains("meeting") || lowerText.contains("schedule")) {
            return "schedule";
        }
        return "general";
    }
}
