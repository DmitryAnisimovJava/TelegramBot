package com.mergeteam.service;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.service.exception.UploadFileException;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {

    AppDocument processDoc(Message externalMessage) throws UploadFileException;
}
