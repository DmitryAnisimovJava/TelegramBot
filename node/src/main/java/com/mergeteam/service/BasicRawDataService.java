package com.mergeteam.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BasicRawDataService {

    void processMessage(Update update);
}
