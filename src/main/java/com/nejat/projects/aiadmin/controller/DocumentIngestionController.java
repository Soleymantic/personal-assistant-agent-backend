package com.nejat.projects.aiadmin.controller;

import com.nejat.projects.aiadmin.service.agent.AiDocumentIngestionService;
import com.nejat.projects.aiadmin.web.DocumentMapper;
import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ingestion")
public class DocumentIngestionController {

    private final AiDocumentIngestionService aiDocumentIngestionService;
    private final DocumentMapper documentMapper;

    public DocumentIngestionController(AiDocumentIngestionService aiDocumentIngestionService,
                                       DocumentMapper documentMapper) {
        this.aiDocumentIngestionService = aiDocumentIngestionService;
        this.documentMapper = documentMapper;
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDetailDto ingestFromEmail(@RequestBody EmailIngestionRequest request) {
        if (request == null || !StringUtils.hasText(request.emailText())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email text is required");
        }
        return documentMapper.toDetailDto(aiDocumentIngestionService.ingestFromEmail(request.emailText()));
    }

    public record EmailIngestionRequest(String emailText) {
    }
}
