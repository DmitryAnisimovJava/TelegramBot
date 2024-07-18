package com.mergeteam.service.impl.controller;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/file")
@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-document")
    public ResponseEntity<FileSystemResource> getDoc(@RequestParam("id") String id) {
        //TODO add controller advice
        Optional<AppDocument> docOptional = fileService.getDocument(id);
        if (docOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AppDocument doc = docOptional.get();
        Optional<FileSystemResource> responseResource = fileService.getFileSystemResource(doc.getBinaryFile());
        return responseResource.map(fileSystemResource -> {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(doc.getMimeType()))
                            .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                            .body(fileSystemResource);
                })
                .orElse(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/get-photo")
    public ResponseEntity<FileSystemResource> getPhoto(@RequestParam("id") String id) {
        //TODO add controller advice
        Optional<AppPhoto> photoOptional = fileService.getPhoto(id);
        if (photoOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AppPhoto photo = photoOptional.get();
        Optional<FileSystemResource> responseResource = fileService.getFileSystemResource(photo.getBinaryFile());
        return responseResource.map(fileSystemResource -> {
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .header("Content-disposition", "attachment;")
                            .body(fileSystemResource);
                })
                .orElse(ResponseEntity.internalServerError().build());
    }
}
