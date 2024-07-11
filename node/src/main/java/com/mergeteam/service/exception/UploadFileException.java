package com.mergeteam.service.exception;

import java.net.MalformedURLException;

public class UploadFileException extends RuntimeException {
    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(Throwable e) {
        super(e);
    }

    public UploadFileException(String message, Throwable e) {
        super(message, e);
    }
}
