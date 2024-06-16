package com.mergeteam.controller;

import com.mergeteam.service.UpdateProducer;
import com.mergeteam.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.mergeteam.RabbitQueueName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
        }
        if (update.getMessage() != null) {
            this.distributeMessagesByType(update);
        } else {
            log.error("Received unsupported message type");
        }
    }

    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            this.processTextMessage(update);
        } else if (message.hasDocument()) {
            this.processDocumentMessage(update);
        } else if (message.hasPhoto()) {
            this.processPhotoMessage(update);
        } else {
            this.setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithResponse(update, "Unsupported message type");
        this.setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        SendMessage message = messageUtils.generateSendMessageWithResponse(update,
                                                                           "Content is received. Processing...");
        this.setView(message);
    }

    private void processPhotoMessage(Update update) {
        this.setFileIsReceivedView(update);
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    }

    private void processDocumentMessage(Update update) {
        this.setFileIsReceivedView(update);
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

}
