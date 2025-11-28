package com.nejat.projects.aiadmin.service.llm;

import java.util.Map;

public interface EmailExtractor {
    Map<String, Object> extract(String emailText);
}
