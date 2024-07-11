package com.mergeteam.service.impl;

import com.mergeteam.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.mergeteam.RabbitQueueName.Messages;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    @Override
    @RabbitListener(queues = Messages.TEXT_UPDATE_VALUE)
    public void consumeTextMessageUpdate(Update update) {
        log.info("Message consumed");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = Messages.DOC_UPDATE_VALUE)
    public void consumeDocMessageUpdate(Update update) {
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = Messages.PHOTO_UPDATE_VALUE)
    public void consumePhotoMessageUpdate(Update update) {
        mainService.processPhotoMessage(update);
    }
}
