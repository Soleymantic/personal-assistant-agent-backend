package com.nejat.projects.aiadmin.service.llm;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class FakeEmailClassifier implements EmailClassifier {

    @Override
    public EmailCategory classify(String emailText) {
        if (emailText == null || emailText.isBlank()) {
            return EmailCategory.OTHER;
        }

        String lowerText = emailText.toLowerCase();
        if (lowerText.contains("invoice") || lowerText.contains("bill")) {
            return EmailCategory.INVOICE;
        }
        if (lowerText.contains("contract")) {
            return EmailCategory.CONTRACT;
        }
        if (lowerText.contains("official") || lowerText.contains("letter")) {
            return EmailCategory.OFFICIAL_LETTER;
        }
        if (lowerText.contains("unsubscribe") || lowerText.contains("spam")) {
            return EmailCategory.SPAM;
        }

        return EmailCategory.OTHER;
    }
}
