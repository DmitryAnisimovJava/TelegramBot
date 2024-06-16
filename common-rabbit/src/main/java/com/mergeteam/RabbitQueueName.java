package com.mergeteam;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum RabbitQueueName {
    DOC_MESSAGE_UPDATE(Messages.DOC_UPDATE_VALUE),
    PHOTO_MESSAGE_UPDATE(Messages.PHOTO_UPDATE_VALUE),
    TEXT_MESSAGE_UPDATE(Messages.TEXT_UPDATE_VALUE),
    ANSWER_MESSAGE(Messages.ANSWER_VALUE);

    private final String queueName;

    RabbitQueueName(String queueName) {
        this.queueName = queueName;
        if (!queueName.equals(this.name().toLowerCase())) {
            throw new IllegalArgumentException();
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Messages {
        public static final String DOC_UPDATE_VALUE = "doc_message_update";
        public static final String PHOTO_UPDATE_VALUE = "photo_message_update";
        public static final String TEXT_UPDATE_VALUE = "text_message_update";
        public static final String ANSWER_VALUE = "answer_message";
    }
}
