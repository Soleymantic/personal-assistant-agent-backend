package com.nejat.projects.aiadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

import com.nejat.projects.aiadmin.model.BureauDocument;

public interface BureauDocumentRepository extends JpaRepository<BureauDocument, UUID>, JpaSpecificationExecutor<BureauDocument> {
}
