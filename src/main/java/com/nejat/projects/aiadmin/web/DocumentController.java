package com.nejat.projects.aiadmin.web;

import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.service.DocumentService;
import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import com.nejat.projects.aiadmin.web.dto.DocumentListDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentListDto> listDocuments(@RequestParam Optional<String> text,
                                               @RequestParam Optional<DocumentStatus> status,
                                               @RequestParam Optional<List<String>> tags) {
        return documentService.listDocuments(text, status, tags);
    }

    @GetMapping("/{id}")
    public DocumentDetailDto getDocument(@PathVariable UUID id) {
        return documentService.getDocument(id);
    }

    @PostMapping
    public DocumentDetailDto createDocument(@RequestBody DocumentDetailDto request) {
        return documentService.createDocument(request);
    }

    @PutMapping("/{id}")
    public DocumentDetailDto updateDocument(@PathVariable UUID id, @RequestBody DocumentDetailDto request) {
        return documentService.updateDocument(id, request);
    }

    @PutMapping("/{id}/status")
    public DocumentDetailDto updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        return documentService.updateStatus(id, request.status());
    }

    public record StatusUpdateRequest(DocumentStatus status) {
    }
}
