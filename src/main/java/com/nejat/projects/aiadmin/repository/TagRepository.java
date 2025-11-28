package com.nejat.projects.aiadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nejat.projects.aiadmin.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
