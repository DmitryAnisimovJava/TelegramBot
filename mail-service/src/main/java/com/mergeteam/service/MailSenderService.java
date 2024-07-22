package com.mergeteam.service;

import com.mergeteam.dto.MailParams;

public interface MailSenderService {

    void sendEmail(MailParams mailParams);
}
