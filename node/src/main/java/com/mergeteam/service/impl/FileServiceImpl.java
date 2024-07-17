package com.mergeteam.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.repository.AppDocumentRepository;
import com.mergeteam.repository.AppPhotoRepository;
import com.mergeteam.service.FileService;
import com.mergeteam.service.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.service.file_info.uri}")
    private String fileInfoUri;
    @Value("${telegram.service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentRepository appDocumentRepository;
    private final ObjectMapper objectMapper;
    private final AppPhotoRepository appPhotoRepository;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = this.getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] fileInBytes = getFileInBytes(response);
            AppDocument appDocument = this.buildTransientAppDoc(telegramMessage.getDocument(), fileInBytes);
            return appDocumentRepository.save(appDocument);
        } else {
            throw new UploadFileException("Bad response service" + response);
        }
    }

    @Override
    public List<AppPhoto> processPhotos(Message telegramMessage) throws UploadFileException {
        List<PhotoSize> photos = telegramMessage.getPhoto();
        List<byte[]> photosInBytes = photos.stream()
                .map(PhotoSize::getFileId)
                .map(this::getFilePath)
                .map(FileServiceImpl::checkStatus)
                .map(this::getFileInBytes)
                .toList();
        List<AppPhoto> appPhotos = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            AppPhoto appPhoto = this.buildTransientAppPhoto(photos.get(i), photosInBytes.get(i));
            this.appPhotoRepository.save(appPhoto);
            appPhotos.add(appPhoto);
        }
        return appPhotos;

    }

    private static ResponseEntity<String> checkStatus(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        } else {
            throw new UploadFileException("Bad response service" + response);
        }
    }

    private byte[] getFileInBytes(ResponseEntity<String> response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String filePath = jsonNode.get("result")
                    .get("file_path")
                    .asText();
            return this.downloadFile(filePath);
        } catch (JsonProcessingException e) {
            throw new UploadFileException("Bad response service" + response);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDocument, byte[] fileInBytes) {
        return AppDocument.builder()
                .telegramFileId(telegramDocument.getFileId())
                .binaryFile(fileInBytes)
                .docName(telegramDocument.getFileName())
                .fileSize(telegramDocument.getFileSize())
                .mimeType(telegramDocument.getMimeType())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, byte[] fileInBytes) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryFile(fileInBytes)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUrl = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        try {
            URL url = new URL(fullUrl);
            try (InputStream inputStream = url.openStream();
                 var byteArrayOutputStream = new ByteArrayOutputStream()) {
                byte[] buf = new byte[1024 * 1024];
                while (inputStream.read(buf) != -1) {
                    byteArrayOutputStream.write(buf);
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (MalformedURLException e) {
            throw new UploadFileException("Telegram file Url not found", e);
        } catch (IOException e) {
            throw new UploadFileException(e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(new HttpHeaders());
        return restTemplate.exchange(this.fileInfoUri,
                                     HttpMethod.GET,
                                     request,
                                     String.class,
                                     token, fileId);
    }


}
