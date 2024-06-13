package com.mergeteam.service;

import com.mergeteam.RabbitQueueName;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void produce(RabbitQueueName rabbitQueueName, Update update);
}
