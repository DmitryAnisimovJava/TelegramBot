package com.mergeteam.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;

public interface ProducerService {
    void produceAnswer(SendMessage message);
}
