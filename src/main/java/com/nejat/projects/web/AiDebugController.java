package com.nejat.projects.web;

import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.service.agent.AiDocumentIngestionService;
import com.nejat.projects.aiadmin.service.llm.EmailCategory;
import com.nejat.projects.aiadmin.service.llm.EmailClassifier;
import com.nejat.projects.aiadmin.service.llm.EmailExtractor;
import com.nejat.projects.aiadmin.web.DocumentMapper;
import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-debug")
public class AiDebugController {

    private final EmailClassifier emailClassifier;
    private final EmailExtractor emailExtractor;
    private final AiDocumentIngestionService aiDocumentIngestionService;
    private final DocumentMapper documentMapper;

    public AiDebugController(EmailClassifier emailClassifier,
                             EmailExtractor emailExtractor,
                             AiDocumentIngestionService aiDocumentIngestionService,
                             DocumentMapper documentMapper) {
        this.emailClassifier = emailClassifier;
        this.emailExtractor = emailExtractor;
        this.aiDocumentIngestionService = aiDocumentIngestionService;
        this.documentMapper = documentMapper;
    }

    @PostMapping("/classify")
    public Map<String, String> classify(@RequestBody EmailTextRequest request) {
        EmailCategory category = emailClassifier.classify(request.getEmailText());
        return Map.of("category", category.name());
    }

    @PostMapping("/extract")
    public Map<String, Object> extract(@RequestBody EmailTextRequest request) {
        return emailExtractor.extract(request.getEmailText());
    }

    @PostMapping("/ingest")
    public DocumentDetailDto ingest(@RequestBody EmailTextRequest request) {
        BureauDocument document = aiDocumentIngestionService.ingestFromEmail(request.getEmailText());
        return documentMapper.toDetailDto(document);
    }
}
