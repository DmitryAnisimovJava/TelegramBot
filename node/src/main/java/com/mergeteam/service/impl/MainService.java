package com.mergeteam.service.impl;

import com.mergeteam.entity.AppDocument;
import com.mergeteam.entity.AppPhoto;
import com.mergeteam.entity.AppUser;
import com.mergeteam.entity.RawData;
import com.mergeteam.enums.UserState;
import com.mergeteam.repository.AppUserRepository;
import com.mergeteam.repository.RawDataRepository;
import com.mergeteam.service.BasicRawDataService;
import com.mergeteam.service.FileService;
import com.mergeteam.service.ProducerService;
import com.mergeteam.service.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.mergeteam.enums.UserState.BASIC_STATE;
import static com.mergeteam.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.mergeteam.service.enums.ServiceCommands.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MainService implements BasicRawDataService {

    private final RawDataRepository rawDataRepository;
    private final AppUserRepository appUserRepository;
    private final ProducerService producerService;
    private final FileService fileService;

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
    @Transactional
    public void processPhotoMessage(Update update) {
        this.saveRawData(update);
        AppUser appUser = this.findOrSaveAppUser(update);
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        String answer;
        try {
            List<AppPhoto> appPhotos = this.fileService.processPhotos(message);
            //TODO добавить генерацию ссылки
            byte[] binaryFile = appPhotos.get(3).getBinaryFile();
            try {
                BufferedImage read = ImageIO.read(new ByteArrayInputStream(binaryFile));
                ImageIO.write(read, "jpg", new File("C:\\Users\\Anisi\\Desktop\\output.jpg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            answer = """
                    Фото успешно сохранено!
                    Ссылка на скачивание: http://site-download.ru/link""";
        } catch (UploadFileException e) {
            log.error("Photos download exception");
            answer = "К сожалению, загрузка фото не удалась. Повторите попытку";
        }
        this.sendMessage(answer, chatId);
    }

    @Override
    @Transactional
    public void processDocMessage(Update update) {
        this.saveRawData(update);
        AppUser appUser = this.findOrSaveAppUser(update);
        Message telegramMessage = update.getMessage();
        Long chatId = telegramMessage.getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        String answer;
        try {
            AppDocument appDocument = this.fileService.processDoc(telegramMessage);
            //TODO добавить генерацию ссылки
            answer = """
                    Документ успешно сохранен!
                    Ссылка на скачивание: http://site-download.ru/link""";
        } catch (UploadFileException e) {
            log.error("File {} download exception", telegramMessage.getDocument().getFileName());
            answer = "К сожалению, загрузка файла не удалась. Повторите попытку";
        }
        this.sendMessage(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getUserState();
        if (!appUser.isActive()) {
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
