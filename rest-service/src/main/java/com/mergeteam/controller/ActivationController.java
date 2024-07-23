package com.mergeteam.controller;

import com.mergeteam.dto.MailParams;
import com.mergeteam.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ActivationController {

    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<?> activateAccount(@RequestParam("id") String id) {
        boolean activation = userActivationService.activation(id);
        if (activation) {
            return ResponseEntity.ok().body("Активация успешно завершена!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
