package com.mergeteam.service.impl;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.repository.AppDocumentRepository;
import com.mergeteam.repository.AppPhotoRepository;
import com.mergeteam.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;

    @Override
    public Optional<AppDocument> getDocument(String docId) {
        //TODO add encrypter
        long id  = Long.parseLong(docId);
        return appDocumentRepository.findById(id);
    }

    @Override
    public Optional<AppPhoto> getPhoto(String photoId) {
        //TODO add encrypter
        long id  = Long.parseLong(photoId);
        return appPhotoRepository.findById(id);
    }

    @Override
    public Optional<FileSystemResource> getFileSystemResource(byte[] bytes) {
        try {
            //TODO check temp file unique names
            File tempFile = File.createTempFile("tempFile", ".bin");
            tempFile.deleteOnExit();
            System.out.println(tempFile.getAbsolutePath());
            FileUtils.writeByteArrayToFile(tempFile, bytes);
            return Optional.ofNullable(new FileSystemResource(tempFile));
        } catch (IOException e) {
            log.error("File system resource isn`t found.");
            return Optional.empty();
        }
    }
}
