package com.mergeteam.service.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkType {
    GET_DOCUMENT("/file/get-document"),
    GET_PHOTO("/file/get-photo");

    private final String link;

    @Override
    public String toString() {
        return link;
    }
}
