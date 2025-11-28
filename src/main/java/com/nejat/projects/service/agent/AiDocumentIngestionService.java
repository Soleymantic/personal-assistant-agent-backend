package com.nejat.projects.service.agent;

import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.model.Tag;
import com.nejat.projects.aiadmin.repository.BureauDocumentRepository;
import com.nejat.projects.aiadmin.repository.TagRepository;
import com.nejat.projects.service.llm.EmailCategory;
import com.nejat.projects.service.llm.EmailClassifier;
import com.nejat.projects.service.llm.EmailExtractor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AiDocumentIngestionService {

    private final EmailClassifier emailClassifier;
    private final EmailExtractor emailExtractor;
    private final BureauDocumentRepository bureauDocumentRepository;
    private final TagRepository tagRepository;

    public AiDocumentIngestionService(EmailClassifier emailClassifier,
                                      EmailExtractor emailExtractor,
                                      BureauDocumentRepository bureauDocumentRepository,
                                      TagRepository tagRepository) {
        this.emailClassifier = emailClassifier;
        this.emailExtractor = emailExtractor;
        this.bureauDocumentRepository = bureauDocumentRepository;
        this.tagRepository = tagRepository;
    }

    public BureauDocument ingestFromEmail(String emailText) {
        EmailCategory category = emailClassifier.classify(emailText);
        Map<String, Object> data = emailExtractor.extract(emailText);

        String title = getString(data.get("title"), "Unbenanntes Dokument");
        String sender = getString(data.get("sender"), "Unbekannt");
        String amount = getString(data.get("amount"), "");
        String summary = getString(data.get("summary"), "");
        LocalDate dueDate = parseDueDate(data.get("due_date"));
        Set<Tag> tags = resolveTags(data.get("tags"));

        BureauDocument document = BureauDocument.builder()
                .id(UUID.randomUUID())
                .title(title)
                .sender(sender)
                .amount(amount)
                .due(dueDate)
                .category(category.name())
                .summary(summary)
                .status(DocumentStatus.PENDING)
                .tags(tags)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return bureauDocumentRepository.save(document);
    }

    private LocalDate parseDueDate(Object dueDateValue) {
        if (dueDateValue instanceof LocalDate localDate) {
            return localDate;
        }

        if (dueDateValue instanceof String dueDateString && !dueDateString.isBlank()) {
            try {
                return LocalDate.parse(dueDateString);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }

        return null;
    }

    private Set<Tag> resolveTags(Object tagsValue) {
        Set<Tag> tags = new HashSet<>();

        if (tagsValue instanceof Iterable<?> iterable) {
            for (Object tagValue : iterable) {
                String tagName = getString(tagValue, null);
                if (tagName == null) {
                    continue;
                }

                String normalized = tagName.trim().toLowerCase();
                if (normalized.isEmpty()) {
                    continue;
                }

                Tag tag = tagRepository.findByName(normalized)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(normalized).build()));
                tags.add(tag);
            }
        }

        return tags;
    }

    private String getString(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        String stringValue = value.toString();
        return stringValue.isBlank() ? defaultValue : stringValue;
    }
}
