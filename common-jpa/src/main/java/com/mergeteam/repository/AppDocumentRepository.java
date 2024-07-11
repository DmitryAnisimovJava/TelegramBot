package com.mergeteam.repository;

import com.mergeteam.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
