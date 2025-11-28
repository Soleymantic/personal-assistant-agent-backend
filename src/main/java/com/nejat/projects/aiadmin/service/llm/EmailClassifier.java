package com.nejat.projects.aiadmin.service.llm;

public interface EmailClassifier {
    EmailCategory classify(String emailText);
}
