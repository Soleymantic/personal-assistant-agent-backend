package com.nejat.projects.aiadmin.service;

import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.model.Tag;
import com.nejat.projects.aiadmin.repository.BureauDocumentRepository;
import com.nejat.projects.aiadmin.repository.TagRepository;
import com.nejat.projects.aiadmin.web.DocumentMapper;
import com.nejat.projects.aiadmin.web.DocumentQueryService;
import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import com.nejat.projects.aiadmin.web.dto.DocumentListDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentQueryService documentQueryService;
    private final BureauDocumentRepository bureauDocumentRepository;
    private final TagRepository tagRepository;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentQueryService documentQueryService,
                           BureauDocumentRepository bureauDocumentRepository,
                           TagRepository tagRepository,
                           DocumentMapper documentMapper) {
        this.documentQueryService = documentQueryService;
        this.bureauDocumentRepository = bureauDocumentRepository;
        this.tagRepository = tagRepository;
        this.documentMapper = documentMapper;
    }

    public List<DocumentListDto> listDocuments(Optional<String> text,
                                               Optional<DocumentStatus> status,
                                               Optional<List<String>> tags) {
        List<BureauDocument> documents = documentQueryService.findDocuments(text, status, tags);
        return documents.stream()
                .map(documentMapper::toListDto)
                .toList();
    }

    public DocumentDetailDto getDocument(UUID id) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        return documentMapper.toDetailDto(document);
    }

    public DocumentDetailDto createDocument(DocumentDetailDto request) {
        BureauDocument document = new BureauDocument();
        Instant now = Instant.now();
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        applyDtoToEntity(request, document);
        BureauDocument saved = bureauDocumentRepository.save(document);
        return documentMapper.toDetailDto(saved);
    }

    public DocumentDetailDto updateDocument(UUID id, DocumentDetailDto request) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        applyDtoToEntity(request, document);
        document.setUpdatedAt(Instant.now());
        BureauDocument saved = bureauDocumentRepository.save(document);
        return documentMapper.toDetailDto(saved);
    }

    public DocumentDetailDto updateStatus(UUID id, DocumentStatus status) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        document.setStatus(status);
        document.setUpdatedAt(Instant.now());
        BureauDocument saved = bureauDocumentRepository.save(document);
        return documentMapper.toDetailDto(saved);
    }

    private void applyDtoToEntity(DocumentDetailDto request, BureauDocument document) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document payload is required");
        }

        document.setTitle(request.getTitle());
        document.setSender(request.getSender());
        document.setAmount(request.getAmount());
        document.setCategory(request.getCategory());
        document.setSummary(request.getSummary());

        document.setDue(parseDate(request.getDue()));
        document.setStatus(parseStatus(request.getStatus()));
        document.setTags(resolveTags(request.getTags()));
    }

    private LocalDate parseDate(String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format for due: " + date);
        }
    }

    private DocumentStatus parseStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        try {
            return DocumentStatus.valueOf(status.trim());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + status);
        }
    }

    private Set<Tag> resolveTags(List<String> names) {
        if (names == null || names.isEmpty()) {
            return new HashSet<>();
        }
        return names.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
    }

    private Tag findOrCreateTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
    }
}
