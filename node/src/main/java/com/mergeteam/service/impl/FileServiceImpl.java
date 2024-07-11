package com.mergeteam.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mergeteam.entity.AppDocument;
import com.mergeteam.repository.AppDocumentRepository;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public AppDocument processDoc(Message externalMessage) {
        String fileId = externalMessage.getDocument().getFileId();
        ResponseEntity<String> response = this.getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String filePath = jsonNode.get("result")
                    .get("file_path")
                    .asText();
            byte[] fileInBytes = this.downloadFile(filePath);
            AppDocument appDocument = this.buildTransientAppDoc(externalMessage.getDocument(), fileInBytes);
            return appDocumentRepository.save(appDocument);
        } else {
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
