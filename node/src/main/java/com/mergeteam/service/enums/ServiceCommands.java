package com.mergeteam.service.enums;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;

    @Override
    public String toString() {
        return cmd;
    }

    public Optional<ServiceCommands> fromValue(String value) {
        return Arrays.stream(ServiceCommands.values())
                .filter(serviceCommands -> serviceCommands.cmd.equals(value))
                .findFirst();
    }
}
