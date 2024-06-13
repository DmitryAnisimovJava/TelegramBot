package com.mergeteam;

import lombok.Getter;

@Getter
public enum RabbitQueueName {
    DOC_MESSAGE_UPDATE("doc_message_update"),
    PHOTO_MESSAGE_UPDATE("photo_message_update"),
    TEXT_MESSAGE_UPDATE("text_message_update"),
    ANSWER_MESSAGE("answer_message");

    private final String queueName;

    RabbitQueueName(String queueName) {
        this.queueName = queueName;
    }
}
