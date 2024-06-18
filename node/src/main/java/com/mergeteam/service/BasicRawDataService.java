package com.mergeteam.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BasicRawDataService {

    void processTextMessage(Update update);
    void processPhotoMessage(Update update);
    void processDocMessage(Update update);
}
