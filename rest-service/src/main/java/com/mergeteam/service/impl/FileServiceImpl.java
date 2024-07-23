package com.mergeteam.service.impl;

import com.mergeteam.crypto.CryptoTool;
import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.repository.AppDocumentRepository;
import com.mergeteam.repository.AppPhotoRepository;
import com.mergeteam.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final CryptoTool cryptoTool;

    @Override
    public Optional<AppDocument> getDocument(String hash) {
        Long id  = cryptoTool.idOf(hash);
        return id != null ? appDocumentRepository.findById(id) : Optional.empty();
    }

    @Override
    public Optional<AppPhoto> getPhoto(String hash) {
        Long id  = cryptoTool.idOf(hash);
        return id != null ? appPhotoRepository.findById(id) : Optional.empty();
    }
}
