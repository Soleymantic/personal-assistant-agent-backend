package com.nejat.projects.aiadmin.web;

import com.nejat.projects.aiadmin.model.BureauDocument;
import com.nejat.projects.aiadmin.model.DocumentStatus;
import com.nejat.projects.aiadmin.model.Tag;
import com.nejat.projects.aiadmin.repository.BureauDocumentRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentQueryService {

    private final BureauDocumentRepository bureauDocumentRepository;

    public DocumentQueryService(BureauDocumentRepository bureauDocumentRepository) {
        this.bureauDocumentRepository = bureauDocumentRepository;
    }

    public List<BureauDocument> findDocuments(Optional<String> text,
                                              Optional<DocumentStatus> status,
                                              Optional<List<String>> tags) {
        Specification<BureauDocument> specification = Specification.where(null);

        if (text.filter(StringUtils::hasText).isPresent()) {
            String keyword = "%" + text.get().trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) -> {
                var lowerTitle = cb.lower(root.get("title"));
                var lowerSender = cb.lower(root.get("sender"));
                var lowerSummary = cb.lower(root.get("summary"));
                return cb.or(
                        cb.like(lowerTitle, keyword),
                        cb.like(lowerSender, keyword),
                        cb.like(lowerSummary, keyword)
                );
            });
        }

        if (status.isPresent()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status.get()));
        }

        if (tags.filter(list -> !list.isEmpty()).isPresent()) {
            List<String> tagNames = tags.get().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .toList();

            if (!tagNames.isEmpty()) {
                specification = specification.and((root, query, cb) -> {
                    query.distinct(true);
                    Join<BureauDocument, Tag> tagJoin = root.join("tags", JoinType.INNER);
                    query.groupBy(root.get("id"));
                    query.having(cb.equal(cb.countDistinct(tagJoin.get("name")), tagNames.size()));
                    return tagJoin.get("name").in(tagNames);
                });
            }
        }

        return bureauDocumentRepository.findAll(specification);
    }
}
