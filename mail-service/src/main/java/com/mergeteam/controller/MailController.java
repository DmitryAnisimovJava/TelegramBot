package com.mergeteam.controller;

import com.mergeteam.dto.MailParams;
import com.mergeteam.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailSenderService mailSenderService;

    @PostMapping("/send")
    public ResponseEntity<String> sendActivationEmail(@RequestBody MailParams mailParams) {
        mailSenderService.sendEmail(mailParams);
        return ResponseEntity.ok().build();
    }
}
