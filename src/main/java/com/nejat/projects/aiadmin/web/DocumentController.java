package com.nejat.projects.aiadmin.web;

import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import com.nejat.projects.aiadmin.web.dto.DocumentListDto;
import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.model.Tag;
import com.nejat.projects.aiadmin.repository.BureauDocumentRepository;
import com.nejat.projects.aiadmin.repository.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentQueryService documentQueryService;
    private final BureauDocumentRepository bureauDocumentRepository;
    private final TagRepository tagRepository;
    private final DocumentMapper documentMapper;

    public DocumentController(DocumentQueryService documentQueryService,
                              BureauDocumentRepository bureauDocumentRepository,
                              TagRepository tagRepository,
                              DocumentMapper documentMapper) {
        this.documentQueryService = documentQueryService;
        this.bureauDocumentRepository = bureauDocumentRepository;
        this.tagRepository = tagRepository;
        this.documentMapper = documentMapper;
    }

    @GetMapping
    public List<DocumentListDto> listDocuments(@RequestParam Optional<String> text,
                                               @RequestParam Optional<DocumentStatus> status,
                                               @RequestParam Optional<List<String>> tags) {
        List<BureauDocument> documents = documentQueryService.findDocuments(text, status, tags);
        return documents.stream()
                .map(documentMapper::toListDto)
                .toList();
    }

    @GetMapping("/{id}")
    public DocumentDetailDto getDocument(@PathVariable UUID id) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        return documentMapper.toDetailDto(document);
    }

    @PostMapping
    public DocumentDetailDto createDocument(@RequestBody DocumentDetailDto request) {
        BureauDocument document = new BureauDocument();
        Instant now = Instant.now();
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        applyDtoToEntity(request, document);
        BureauDocument saved = bureauDocumentRepository.save(document);
        return documentMapper.toDetailDto(saved);
    }

    @PutMapping("/{id}")
    public DocumentDetailDto updateDocument(@PathVariable UUID id, @RequestBody DocumentDetailDto request) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        applyDtoToEntity(request, document);
        document.setUpdatedAt(Instant.now());
        BureauDocument saved = bureauDocumentRepository.save(document);
        return documentMapper.toDetailDto(saved);
    }

    @PutMapping("/{id}/status")
    public DocumentDetailDto updateStatus(@PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        BureauDocument document = bureauDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (request == null || request.status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        document.setStatus(request.status);
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

    public record StatusUpdateRequest(DocumentStatus status) {
    }
}
