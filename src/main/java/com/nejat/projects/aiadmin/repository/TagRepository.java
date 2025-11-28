package com.nejat.projects.aiadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nejat.projects.aiadmin.model.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
