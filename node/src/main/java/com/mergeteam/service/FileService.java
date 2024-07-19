package com.mergeteam.service;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.service.enums.LinkType;
import com.mergeteam.service.exception.UploadFileException;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface FileService {

    AppDocument processDoc(Message telegramMessage) throws UploadFileException;

    List<AppPhoto> processPhotos(Message telegramMessage) throws UploadFileException;

    String generateLink(Long id, LinkType linkType);
}
