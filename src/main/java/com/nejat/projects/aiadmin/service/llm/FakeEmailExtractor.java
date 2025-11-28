package com.nejat.projects.aiadmin.service.llm;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("dev")
public class FakeEmailExtractor implements EmailExtractor {

    @Override
    public Map<String, Object> extract(String emailText) {
        Map<String, Object> extracted = new HashMap<>();
        extracted.put("title", "Email from AI pipeline");
        extracted.put("sender", "unknown@example.com");
        extracted.put("amount", "$0.00");
        extracted.put("due_date", LocalDate.now().plusDays(14).toString());
        extracted.put("summary", emailText != null ? emailText : "No content");
        extracted.put("tags", List.of("auto-generated", "dev"));
        return extracted;
    }
}
