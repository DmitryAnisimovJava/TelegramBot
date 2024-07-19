package com.mergeteam.controller;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.mergeteam.controller.FileController.REQUEST_MAPPING;

@RequestMapping(REQUEST_MAPPING)
@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {

    public static final String REQUEST_MAPPING = "/file";
    public static final String GET_DOCUMENT = "/get-document";
    public static final String GET_PHOTO = "/get-photo";

    private final FileService fileService;

    @GetMapping(GET_DOCUMENT)
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        //TODO add controller advice
        Optional<AppDocument> docOptional = fileService.getDocument(id);
        return docOptional.map(doc -> {
                    byte[] binaryFile = doc.getBinaryFile();
                    return binaryFile.length > 0 ? ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(doc.getMimeType()))
                            .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                            .body(new ByteArrayResource(binaryFile))
                            : ResponseEntity.internalServerError().build();
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping(GET_PHOTO)
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        //TODO add controller advice
        Optional<AppPhoto> photoOptional = fileService.getPhoto(id);
        return photoOptional.map(photo -> {
                    byte[] binaryFile = photo.getBinaryFile();
                    return binaryFile.length > 0 ? ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .header("Content-disposition", "attachment;")
                            .body(new ByteArrayResource(photo.getBinaryFile()))
                            : ResponseEntity.internalServerError().build();
                })
                .orElse(ResponseEntity.badRequest().build());
    }
}
