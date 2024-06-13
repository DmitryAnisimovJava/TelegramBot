package com.mergeteam.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.mergeteam.RabbitQueueName.*;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {

    private static final String EXCHANGE_NAME = "telegram_bot_exchange";
    private final AmqpAdmin amqpAdmin;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void createQueues() {
        DirectExchange directExchange = new DirectExchange(EXCHANGE_NAME, true, false);
        amqpAdmin.declareExchange(directExchange);
        Queue answerQueue = new Queue(ANSWER_MESSAGE.getQueueName());
        Queue photoQueue = new Queue(PHOTO_MESSAGE_UPDATE.getQueueName());
        Queue docQueue = new Queue(DOC_MESSAGE_UPDATE.getQueueName());
        Queue messageQueue = new Queue(TEXT_MESSAGE_UPDATE.getQueueName());

        amqpAdmin.declareQueue(answerQueue);
        amqpAdmin.declareQueue(photoQueue);
        amqpAdmin.declareQueue(docQueue);
        amqpAdmin.declareQueue(messageQueue);
        amqpAdmin.declareBinding(BindingBuilder.bind(answerQueue)
                                         .to(directExchange)
                                         .withQueueName());
        amqpAdmin.declareBinding(BindingBuilder.bind(photoQueue)
                                         .to(directExchange)
                                         .withQueueName());
        amqpAdmin.declareBinding(BindingBuilder.bind(docQueue)
                                         .to(directExchange)
                                         .withQueueName());
        amqpAdmin.declareBinding(BindingBuilder.bind(messageQueue)
                                         .to(directExchange)
                                         .withQueueName());
    }
}
