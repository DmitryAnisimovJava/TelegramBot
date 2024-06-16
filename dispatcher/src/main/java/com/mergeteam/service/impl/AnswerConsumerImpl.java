package com.mergeteam.service.impl;

import com.mergeteam.RabbitQueueName;
import com.mergeteam.controller.UpdateController;
import com.mergeteam.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.mergeteam.RabbitQueueName.*;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = Messages.ANSWER_VALUE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
