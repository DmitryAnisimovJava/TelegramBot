package com.mergeteam.service.impl;

import com.mergeteam.entity.AppUser;
import com.mergeteam.entity.RawData;
import com.mergeteam.entity.enums.UserState;
import com.mergeteam.entity.service.AppUserRepository;
import com.mergeteam.repository.RawDataRepository;
import com.mergeteam.service.BasicRawDataService;
import com.mergeteam.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.mergeteam.entity.enums.UserState.*;

@Service
@RequiredArgsConstructor
public class TextMessageService implements BasicRawDataService {

    private final RawDataRepository rawDataRepository;
    private final AppUserRepository appUserRepository;
    private final ProducerService producerService;
    @Override
    public void processMessage(Update update) {
        this.saveRawData(update);
        Message textMessage = update.getMessage();
        User user = textMessage.getFrom();
        this.findOrSaveAppUser(user);
        SendMessage answer = SendMessage.builder()
                .chatId(textMessage.getChatId())
                .text("Hello from Node text")
                .build();
        producerService.produceAnswer(answer);
    }

    private AppUser findOrSaveAppUser(User telegramUser) {
        return appUserRepository.findAppUserByTelegramUserId(telegramUser.getId())
                .orElseGet(() -> {
                    AppUser user = AppUser.builder()
                            .telegramUserId(telegramUser.getId())
                            .firstName(telegramUser.getFirstName())
                            .lastName(telegramUser.getLastName())
                            .username(telegramUser.getUserName())
                            .userState(BASIC_STATE)
                            //TODO change later, when registration created
                            .isActive(true)
                            .build();
                    return appUserRepository.save(user);
                });
    }
    private void saveRawData(Update update) {
        RawData data = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(data);
    }
}
