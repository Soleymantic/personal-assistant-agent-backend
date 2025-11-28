package com.nejat.projects.aiadmin.service.agent;

import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.model.Tag;
import com.nejat.projects.aiadmin.repository.BureauDocumentRepository;
import com.nejat.projects.aiadmin.repository.TagRepository;
import com.nejat.projects.aiadmin.service.llm.EmailCategory;
import com.nejat.projects.aiadmin.service.llm.EmailClassifier;
import com.nejat.projects.aiadmin.service.llm.EmailExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiDocumentIngestionService {

    private final BureauDocumentRepository bureauDocumentRepository;
    private final TagRepository tagRepository;
    private final EmailClassifier emailClassifier;
    private final EmailExtractor emailExtractor;

    @Transactional
    public BureauDocument ingestFromEmail(String emailText) {
        EmailCategory category = emailClassifier.classify(emailText);
        Map<String, Object> extracted = emailExtractor.extract(emailText);

        Instant now = Instant.now();
        BureauDocument document = new BureauDocument();
        document.setId(UUID.randomUUID());
        document.setTitle(asString(extracted.get("title")));
        document.setSender(asString(extracted.get("sender")));
        document.setAmount(asString(extracted.get("amount")));
        document.setDue(parseDueDate(extracted.get("due_date")));
        document.setCategory(category.name());
        document.setSummary(asString(extracted.get("summary")));
        document.setStatus(DocumentStatus.PENDING);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        document.setTags(resolveTags(extracted.get("tags")));

        return bureauDocumentRepository.save(document);
    }

    private String asString(Object value) {
        return value != null ? value.toString() : null;
    }

    private LocalDate parseDueDate(Object value) {
        if (value == null) {
            return null;
        }
        String date = value.toString().trim();
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date);
    }

    private Set<Tag> resolveTags(Object value) {
        Set<Tag> resolved = new HashSet<>();
        if (value instanceof Collection<?> collection) {
            for (Object tagValue : collection) {
                String tagName = asString(tagValue);
                if (tagName == null || tagName.isBlank()) {
                    continue;
                }
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                resolved.add(tag);
            }
        }
        return resolved;
    }
}
