package com.mergeteam.service.impl;

import com.mergeteam.entity.AppUser;
import com.mergeteam.entity.RawData;
import com.mergeteam.entity.enums.UserState;
import com.mergeteam.entity.service.AppUserRepository;
import com.mergeteam.repository.RawDataRepository;
import com.mergeteam.service.BasicRawDataService;
import com.mergeteam.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;
import java.util.function.Function;

import static com.mergeteam.entity.enums.UserState.BASIC_STATE;
import static com.mergeteam.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.mergeteam.service.enums.ServiceCommands.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MessageService implements BasicRawDataService {

    private final RawDataRepository rawDataRepository;
    private final AppUserRepository appUserRepository;
    private final ProducerService producerService;

    private final Map<String, Function<AppUser, String>> defineMessage =
            Map.of(HELP.toString(), this::help,
                   REGISTRATION.toString(), this::registration,
                   START.toString(), this::hello);

    @Override
    @Transactional
    public void processTextMessage(Update update) {
        this.saveRawData(update);
        AppUser appUser = this.findOrSaveAppUser(update);
        UserState userState = appUser.getUserState();
        String text = update.getMessage().getText();
        String output = "";
        if (CANCEL.toString().equals(text)) {
            output = this.cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = this.defineMessage.getOrDefault(text, this::commandNotFound).apply(appUser);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO create email sender
        } else {
            log.error("Unknown user state: {}", userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
        }
        Long chatId = update.getMessage().getChatId();
        this.sendMessage(output, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        this.saveRawData(update);
        AppUser appUser = this.findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        //TODO add photo save method

        String answer = "Photo succefully saved";
        this.sendMessage(answer, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        this.saveRawData(update);
        AppUser appUser = this.findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        //TODO add doc save method

        String answer = "Doc succefully saved";
        this.sendMessage(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            String error = "Please registry or confirm your email for access to send photo or documents";
            this.sendMessage(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            String error = "Cancel your current operation with /cancel command and try again";
            this.sendMessage(error, chatId);
            return true;
        }
        return false;
    }

    private void sendMessage(String output, Long chatId) {
        SendMessage answer = SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build();
        producerService.produceAnswer(answer);
    }

    public AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
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

    private String hello(AppUser appUser) {
        return null;
    }

    private String registration(AppUser appUser) {
        //TODO регистрация
        return "Сделать регистрацию";
    }


    private String help(AppUser appUser) {
        return """
                Список доступных команд:
                /cancel - отмена выполнения текущей команды;
                /registration - регистрация пользователя""";
    }

    private String commandNotFound(AppUser user) {
        return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
    }

    private String cancelProcess(AppUser user) {
        user.setUserState(BASIC_STATE);
        appUserRepository.flush();
        return "Команда отменена!";
    }
}
