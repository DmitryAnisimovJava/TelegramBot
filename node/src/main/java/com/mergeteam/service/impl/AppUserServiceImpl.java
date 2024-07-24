package com.mergeteam.service.impl;

import com.mergeteam.crypto.CryptoTool;
import com.mergeteam.dto.MailParams;
import com.mergeteam.entity.AppUser;
import com.mergeteam.enums.UserState;
import com.mergeteam.repository.AppUserRepository;
import com.mergeteam.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final CryptoTool cryptoTool;
    private final EmailValidator emailValidator;
    @Value("${telegram.service.mail.uri}")
    private String mailServiceUri;

    @Transactional
    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return """
                    Вам на почту уже отправлено письмо.
                    Перейдите по ссылке в письме для подтверждения регистрации.""";
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        this.appUserRepository.save(appUser);
        return "Введите, пожалуйста, ваш email";
    }

    @Transactional
    @Override
    public String setEmail(AppUser appUser, String email) {
        boolean valid = emailValidator.isValid(email);
        if (!valid) {
            return "Введите пожалуйста корректный email. Для отмены команды введите /cancel";
        }

        Optional<AppUser> userOptional = appUserRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setUserState(UserState.BASIC_STATE);
            appUserRepository.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<?> response = this.sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String msg = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserRepository.save(appUser);
                return msg;
            }
            return """
                    Вам на почту было отправлено письмо.
                    Перейдите по ссылке для подтверждения регистрации""";
        } else {
            return """
                    Этот email уже используется.
                    Введите корректный email. Для отмены команды введите /cancel
                    """;
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> mailParamsHttpEntity = new HttpEntity<>(mailParams, httpHeaders);
        return restTemplate.exchange(mailServiceUri,
                                     HttpMethod.POST,
                                     mailParamsHttpEntity,
                                     String.class);
    }

}
