package com.nejat.projects.aiadmin.web;

import com.nejat.projects.aiadmin.web.dto.DocumentDetailDto;
import com.nejat.projects.aiadmin.web.dto.DocumentListDto;
import com.nejat.projects.aiadmin.model.BureauDocument;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentMapper {

    public DocumentListDto toListDto(BureauDocument doc) {
        if (doc == null) {
            return null;
        }
        return DocumentListDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .sender(doc.getSender())
                .amount(doc.getAmount())
                .due(doc.getDue() != null ? doc.getDue().toString() : null)
                .status(doc.getStatus() != null ? doc.getStatus().name() : null)
                .category(doc.getCategory())
                .tags(extractTagNames(doc))
                .summary(doc.getSummary())
                .build();
    }

    public DocumentDetailDto toDetailDto(BureauDocument doc) {
        if (doc == null) {
            return null;
        }
        return DocumentDetailDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .sender(doc.getSender())
                .amount(doc.getAmount())
                .due(doc.getDue() != null ? doc.getDue().toString() : null)
                .status(doc.getStatus() != null ? doc.getStatus().name() : null)
                .category(doc.getCategory())
                .tags(extractTagNames(doc))
                .summary(doc.getSummary())
                .build();
    }

    private List<String> extractTagNames(BureauDocument doc) {
        if (doc.getTags() == null) {
            return List.of();
        }
        return doc.getTags().stream()
                .filter(tag -> tag.getName() != null)
                .map(tag -> tag.getName().trim())
                .filter(name -> !name.isEmpty())
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
