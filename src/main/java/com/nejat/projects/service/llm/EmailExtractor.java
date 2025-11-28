package com.nejat.projects.service.llm;

import java.util.Map;

public interface EmailExtractor {
    Map<String, Object> extract(String emailText);
}
