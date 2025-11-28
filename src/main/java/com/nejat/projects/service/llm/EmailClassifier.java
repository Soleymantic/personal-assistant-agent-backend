package com.nejat.projects.service.llm;

public interface EmailClassifier {
    EmailCategory classify(String emailText);
}
