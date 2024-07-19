package com.mergeteam.service;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import org.springframework.core.io.FileSystemResource;

import java.util.Optional;

public interface FileService {

    Optional<AppDocument> getDocument(String id);

    Optional<AppPhoto> getPhoto(String id);

}
