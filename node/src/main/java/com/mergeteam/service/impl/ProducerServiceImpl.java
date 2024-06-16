package com.mergeteam.service.impl;

import com.mergeteam.RabbitQueueName;
import com.mergeteam.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.mergeteam.RabbitQueueName.*;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;
    @Override
    public void produceAnswer(SendMessage message) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE.getQueueName(), message);
    }
}
