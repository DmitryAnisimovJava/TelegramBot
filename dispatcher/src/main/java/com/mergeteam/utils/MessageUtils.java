package com.mergeteam.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateSendMessageWithResponse(Update update, String response) {
        Message message = update.getMessage();
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(response)
                .build();
    }
}
