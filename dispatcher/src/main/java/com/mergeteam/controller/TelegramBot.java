package com.mergeteam.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@Setter
public class TelegramBot extends TelegramWebhookBot {

    private String username;
    private String botName;
    private String botUri;
    private final UpdateProcessor updateProcessor;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.username}") String username,
                       @Value("${telegram.bot.name}") String botName,
                       @Value("${telegram.bot.uri}") String botUri,
                       UpdateProcessor updateProcessor) {
        super(botToken);
        this.username = username;
        this.botName = botName;
        this.botUri = botUri;
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
        SetWebhook webhook = SetWebhook.builder()
                .url(this.botUri)
                .build();
        try {
            this.setWebhook(webhook);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                this.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }
}
